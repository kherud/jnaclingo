package org.potassco.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration for entries of the statistics.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_statistics_type_e}
 */
public enum StatisticsType {
    /** the entry is invalid (has neither of the types below) */
    EMPTY(0),
    /** the entry is a (double) value */
    VALUE(1),
    /** the entry is an array */
    ARRAY(2),
    /** the entry is a map */
    MAP(3);

    private static Map<Integer, StatisticsType> mapping = new HashMap<>();
    
	static {
	    for (StatisticsType solveEventType : StatisticsType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static StatisticsType fromValue(int type) {
		return mapping.get(type);
	}

    private int type;

    private StatisticsType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
