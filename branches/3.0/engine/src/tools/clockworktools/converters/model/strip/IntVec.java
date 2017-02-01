

package clockworktools.converters.model.strip;



public class IntVec {

    private int[] data;
    private int count = 0;
    
    public IntVec() {
        data = new int[16];
    }
    
    public IntVec(int startSize) {
        data = new int[startSize];
    }
    
    public int size() {
        return count;
    }
    
    public int get(int i) {
        return data[i];
    }
    
    public void add(int val) {
        if ( count == data.length ) {
            int[] ndata = new int[count*2];
            System.arraycopy(data,0,ndata,0,count);
            data = ndata;
        }
        data[count] = val;
        count++;
    }
    
    public void clear() {
        count = 0;
    }
}
