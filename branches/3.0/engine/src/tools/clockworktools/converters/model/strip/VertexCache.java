

package clockworktools.converters.model.strip;

import java.util.Arrays;


class VertexCache {

    int[] entries;
    int numEntries;
    
    public VertexCache() {
        this(16);
    }
    
    public VertexCache(int size) {
        numEntries = size;
        entries = new int[numEntries];
        clear();
    }
    
    public boolean inCache(int entry) {
        for(int i = 0; i < numEntries; i++)
        {
            if(entries[i] == entry)
            {
                return true;
            }
        }
        return false;
    }
    
    public int addEntry(int entry) {
        int removed;
        
        removed = entries[numEntries - 1];
        
        //push everything right one
        for(int i = numEntries - 2; i >= 0; i--)
           {
            entries[i + 1] = entries[i];
        }
        
        entries[0] = entry;
        
        return removed;
    }

    public void clear() {
        Arrays.fill(entries,-1);
    }
    
    public int at(int index) {
        return entries[index];
    }
    
    public void set(int index, int value) {
        entries[index] = value;
    }
        
    public void copy(VertexCache inVcache)
    {
        for(int i = 0; i < numEntries; i++)
           {
            inVcache.set(i, entries[i]);
        }
    }

}
