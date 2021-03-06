/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovy.stream.iterators ;

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

public class MapIterator<T,U> extends AbstractIterator<Map<T,U>> {
    private final Map<T,Iterable<U>> iterables ;
    private final Map<T,Iterator<U>> iterators ;
    private final List<T>            keys ;

    public MapIterator( Map<T,? extends Iterable<U>> underlying ) {
        super( null ) ;
        this.iterables = new LinkedHashMap<T,Iterable<U>>();
        this.iterators = new LinkedHashMap<T,Iterator<U>>();
        this.keys = new ArrayList<T>();
        for( Map.Entry<T,? extends Iterable<U>> entry : underlying.entrySet() ) {
            keys.add( entry.getKey() ) ;
            iterables.put( entry.getKey(), entry.getValue() ) ;
            iterators.put( entry.getKey(), entry.getValue().iterator() ) ;
        }
    }

    private void loadFirst() {
        current = new LinkedHashMap<T,U>() ;
        for( T key : keys ) {
            current.put( key, iterators.get( key ).next() ) ;
        }
    }

    @Override
    protected void loadNext() {
        if( current == null ) {
            loadFirst() ;
        }
        else {
            current = new LinkedHashMap<T,U>( current ) ;
            for( int i = keys.size() - 1 ; i >= 0 ; i-- ) {
                T key = keys.get( i ) ;
                if( iterators.get( key ).hasNext() ) {
                    current.put( key, iterators.get( key ).next() ) ;
                    break ;
                }
                else if( i > 0 ) {
                    iterators.put( key, iterables.get( key ).iterator() ) ;
                    current.put( key, iterators.get( key ).next() ) ;
                }
                else {
                    exhausted = true ;
                }
            }
        }
    }
}