package org.potassco.clingo.statistics;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

public class StatisticsArray extends Statistics {

    public StatisticsArray(Pointer statistics, long key) {
        super(statistics, key);
    }

    public StatisticsArray(Pointer statistics, long key, double[] values) {
        super(statistics, key);
    }

    /**
     * Get the size of an array entry.
     *
     * @return the resulting size
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_array_size(statistics, key, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }


    /**
     * Get the value at the given index of an array entry.
     *
     * @param index the index of the entry
     * @return the value at the index
     */
    @Override
    public Statistics get(int index) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_array_at(statistics, key, new NativeSize(index), longByReference));
        return fromKey(statistics, longByReference.getValue());
    }

    /**
     * Create the subkey at the end of an array entry.
     *
     * @param type the type of the new subkey
     * @return the resulting statistics object
     */
    public Statistics add(StatisticsType type) {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_statistics_array_push(statistics, key, type.getValue(), longByReference);
        return Statistics.fromKey(statistics, longByReference.getValue(), type);
    }

    /**
     * Adds an array of raw values to this entry
     *
     * @param values the raw values
     */
    public void add(double[] values) {
        LongByReference longByReference = new LongByReference();
        for (double value : values) {
            Clingo.check(Clingo.INSTANCE.clingo_statistics_array_push(statistics, key, StatisticsType.VALUE.getValue(), longByReference));
            Clingo.check(Clingo.INSTANCE.clingo_statistics_value_set(statistics, longByReference.getValue(), value));
        }
    }

    /**
     * @return the type of this statistics object
     */
    @Override
    public StatisticsType getType() {
        return StatisticsType.ARRAY;
    }
}
