/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package engine3.iterators;

import org.apache.commons.logging.*;

import com.hp.hpl.jena.query.engine.Binding;
import com.hp.hpl.jena.query.engine.QueryIterator;
import com.hp.hpl.jena.query.engine1.ExecutionContext;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecException;

/** Iterator until a limit is reached. 
 * 
 * @author Andy Seaborne
 * @version $Id: QueryIterLimitOffset.java,v 1.6 2007/01/02 11:19:31 andy_seaborne Exp $
 */

public class QueryIterLimitOffset extends QueryIter
{
    static Log clsLog = LogFactory.getLog(QueryIterLimitOffset.class) ;
    long count = 0 ;
    long limit ;
    long offset ;
    QueryIterator base ;
    
    /** Create an iterator that limits the number of returns of
     * another CloseableIterator.
     * 
     * @param cIter            The closable iterator to throttle 
     * @param startPosition    Offset of start after - 0 is the no-op.
     * @param numItems         Maximium number of items to yield.  
     */
    
    public QueryIterLimitOffset(QueryIterator cIter, long startPosition, long numItems, ExecutionContext context)
    {
        super(context) ;
        
        offset = startPosition ;
        if ( offset == Query.NOLIMIT )
            offset = 0 ;
        
        limit = numItems ;
        if ( limit == Query.NOLIMIT )
            limit = Long.MAX_VALUE ;

        if ( limit < 0 )
            throw new QueryExecException("Negative LIMIT: "+limit) ;
        if ( offset < 0 )
            throw new QueryExecException("Negative OFFSET: "+offset) ;
        
        base = cIter ;
        count = 0 ;
        // Offset counts from 0 (the no op).
        for ( int i = 0 ; i < offset ; i++ )
        {
            // Not subtle
            if ( !cIter.hasNext() ) { close() ; break ; }
            cIter.next() ;
        }
    }
    
    protected boolean hasNextBinding()
    {
        if ( isFinished() )
            return false;
        
        if ( ! base.hasNext() )
            return false ;
        
        if ( count >= limit )
            return false ;

        return true ;
    }

    protected Binding moveToNextBinding()
    {
        count ++ ;
        return base.nextBinding() ;
    }

    protected void closeIterator()
    {
        if ( base != null )
            base.close() ;
        base = null ;
    }
}

/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */