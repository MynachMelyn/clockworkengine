package checkers.quals;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Refines the qualified type of the annotated field or variable based on the
 * qualified type of the receiver.  The annotation declares a relationship
 * between multiple type qualifier hierarchies.
 *
 * Example:
 * Consider a field, lock, that is only initialised if the
 * enclosing object (the receiver), is marked as ThreadSafe.
 * Such a field can be declared as:
 *
 * 
 *   private @Nullable @Dependent(result=NonNull.class, when=ThreadSafe.class)
 *     Lock lock;
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Dependent {

    /**
     * The class of the refined qualifier to be applied.
     */
    Class<? extends Annotation> result();

    /**
     * The qualifier class of the receiver that causes the result}
     * qualifier to be applied.
     */
    Class<? extends Annotation> when();
}
