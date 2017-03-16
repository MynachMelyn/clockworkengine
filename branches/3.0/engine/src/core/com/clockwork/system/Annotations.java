
package com.clockwork.system;

import checkers.quals.TypeQualifier;
import java.lang.annotation.*;

/**
 * This class contains the Annotation definitions for CW. Mostly these are used
 * for code error checking.
 */
public class Annotations {

    /**
     * Annotation used for math primitive fields, method parameters or method return values.
     * Specifies that the primitve is read only and should not be changed.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @TypeQualifier
    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.TYPE, ElementType.METHOD})
    public @interface ReadOnly {
    }

    /**
     * Annotation used for methods in math primitives that are destructive to the
     * object (xxxLocal, setXXX etc.).
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @TypeQualifier
    @Target({ElementType.METHOD})
    public @interface Destructive {
    }

    /**
     * Annotation used for public methods that are not to be called by users.
     * Examples include update() methods etc.
     */
    public @interface Internal {
    }
}
