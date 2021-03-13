import java.lang.reflect.Array;
import java.util.*;

public class NativeCache<T> {
    public final int size;
    public String [] slots;
    public T [] values;
    public int [] hits;
    private final int STEP = 3;

    public NativeCache(int sz, Class clazz)
    {
        size = sz;
        slots = new String[size];
        hits = new int[size];        
        values = (T[]) Array.newInstance(clazz, this.size);
    }

    public int hashFun(String key)
    {
        byte[] bytes = key.getBytes();
        int sum = IntStream.range(0, bytes.length).map(x -> bytes[x]).sum();
        return sum % getSize();
    }

    public int seekSlots(String key)
    {
        int slot = hashFun(key);

        for (int i = 0; i< STEP; i++) {
            while (slot < getSize()) {
                if (getSlots()[slot] == null
                        || key.equals(getSlots()[slot])) {
                    return slot;
                }
                slot += STEP;
            }
            slot = slot - getSize();
        }

        // Cache is full. Search and clean with minimum hit
        slot = findMinimumHit();
        remove(slot);
        return slot;
    }

    //Delete the element
    public void remove(int indexElementDelete) {
        getHits()[indexElementDelete] = 0;
        getSlots()[indexElementDelete] = null;
        getValues()[indexElementDelete] = null;
    }

    //Find the element with minimum hit
    public int findMinimumHit() {
        int indexMinHit = 0;
        int minHit = getHits()[indexMinHit];

        for (int i = 1; i < size; i++) {
            if (minHit > getHits()[i]) {
                minHit = getHits()[i];
                indexMinHit = i;
            }
        }
        return indexMinHit;
    }

/*    public boolean is_key(String key) {
        int slot = hashFun(key);

        for (int i = 0; i< STEP; i++) {
            while (slot < getSize()) {
                if (getSlots()[slot] == null) {
                    return false;
                }
                if (key.equals(getSlots()[slot])) {
                    return true;
                }
                slot += STEP;
            }
            slot = slot - getSize();
        }
        return false;
    }*/

    public void put(String key, T value)
    {
        int slot = seekSlots(key);

        if (slot != -1) {
            getSlots()[slot] = key;
            getValues()[slot] = value;
            getHits()[slot] = 0;
        }
    }

    public T get(String key)
    {
        int slot = hashFun(key);

        for (int i = 0; i< STEP; i++) {
            while (slot < getSize()) {
                if (getSlots()[slot] == null) {
                    return null;
                }
                if (key.equals(getSlots()[slot])) {
                    getHits()[slot]++;
                    return getValues()[slot];
                }
                slot += STEP;
            }
            slot = slot - getSize();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NativeCache nativeCache = (NativeCache) o;

        if (size != nativeCache.size) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(slots, nativeCache.slots)
                && Arrays.equals(values, nativeCache.values)
                && Arrays.equals(hits, nativeCache.hits);
    }

    public int getSize() {
        return size;
    }

    public int[] getHits() {
        return hits;
    }

    public String[] getSlots() {
        return slots;
    }

    public T[] getValues() {
        return values;
    }
}
