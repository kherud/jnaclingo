package org.potassco.clingo.theory;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of theory term types.
 *
 * @author Josef Schneeberger
 */
public enum TheoryTermType {
    /**
     * a tuple term, e.g., `(1,2,3)`
     */
    TUPLE(0),
    /**
     * a list term, e.g., `[1,2,3]`
     */
    LIST(1),
    /**
     * a set term, e.g., `{1,2,3}`
     */
    SET(2),
    /**
     * a function term, e.g., `f(1,2,3)`
     */
    FUNCTION(3),
    /**
     * a number term, e.g., `42`
     */
    NUMBER(4),
    /**
     * a symbol term, e.g., `c`
     */
    SYMBOL(5);

    private static final Map<Integer, TheoryTermType> mapping = new HashMap<>();

    static {
        for (TheoryTermType solveEventType : TheoryTermType.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static TheoryTermType fromValue(int type) {
        return mapping.get(type);
    }

    private final int type;

    TheoryTermType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
