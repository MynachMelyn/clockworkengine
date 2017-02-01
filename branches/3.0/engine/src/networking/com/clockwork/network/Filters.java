
package com.clockwork.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 *  Static utility methods pertaining to Filter instances.
 *
 *  @version   $Revision$
 */
public class Filters 
{
    /**
     *  Creates a filter that returns true for any value in the specified
     *  list of values and false for all other cases.
     */
    public static <T> Filter<T> in( T... values )
    {
        return in( new HashSet<T>(Arrays.asList(values)) );
    }
    
    /**
     *  Creates a filter that returns true for any value in the specified
     *  collection and false for all other cases.
     */
    public static <T> Filter<T> in( Collection<? extends T> collection )
    {
        return new InFilter<T>(collection);
    }

    /**
     *  Creates a filter that returns true for any value NOT in the specified
     *  list of values and false for all other cases.  This is the equivalent
     *  of calling not(in(values)).
     */
    public static <T> Filter<T> notIn( T... values )
    {
        return not( in( values ) );
    }
    
    /**
     *  Creates a filter that returns true for any value NOT in the specified
     *  collection and false for all other cases.  This is the equivalent
     *  of calling not(in(collection)).
     */
    public static <T> Filter<T> notIn( Collection<? extends T> collection )
    {
      //  return not( in( collection ) );
       return null; //temporary TODO
    }
    
    /**
     *  Creates a filter that returns true for inputs that are .equals()
     *  equivalent to the specified value.
     */
    public static <T> Filter<T> equalTo( T value )
    {
        return new EqualToFilter<T>(value); 
    }     

    /**
     *  Creates a filter that returns true for inputs that are NOT .equals()
     *  equivalent to the specified value.  This is the equivalent of calling
     *  not(equalTo(value)).
     */
    public static <T> Filter<T> notEqualTo( T value )
    {
        return not(equalTo(value));
    }     

    /**
     *  Creates a filter that returns true when the specified delegate filter
     *  returns false, and vice versa.
     */
    public static <T> Filter<T> not( Filter<T> f )
    {
        return new NotFilter<T>(f);
    }
 
    private static class EqualToFilter<T> implements Filter<T>
    {
        private T value;
        
        public EqualToFilter( T value )
        {
            this.value = value;
        }
        
        public boolean apply( T input )
        {
            return value == input || (value != null && value.equals(input));
        }
    }
    
    private static class InFilter<T> implements Filter<T>
    {
        private Collection<? extends T> collection;
        
        public InFilter( Collection<? extends T> collection )
        {
            this.collection = collection;
        }
        
        public boolean apply( T input )
        {
            return collection.contains(input);
        } 
    }
    
    private static class NotFilter<T> implements Filter<T>
    {
        private Filter<T> delegate;
        
        public NotFilter( Filter<T> delegate )
        {
            this.delegate = delegate;
        }
        
        public boolean apply( T input )
        {
            return !delegate.apply(input);
        }
    } 
}


