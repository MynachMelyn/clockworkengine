package checkers.quals;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.FIELD;

/**
 * Declares that the field may not be accessed if the receiver is of the
 * specified qualifier type (or any supertype).
 *
 * This property is verified by the checker that type-checks the
 * when element value qualifier.
 *
 * Example
 * Consider a class, Table, with a locking field, lock.  The
 * lock is used when a Table instance is shared across threads.  When
 * running in a local thread, the lock} field ought not to be used.
 *
 * You can declare this behaviour in the following way:
 *
 * 
 * class Table {
 *   private @Unused(when=LocalToThread.class) final Lock lock;
 *   ...
 * }
 * 
 *
 * The checker for @LocalToThread would issue an error for the following code:
 *
 *   @LocalToThread Table table = ...;
 *   ... table.lock ...;
 * 
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface Unused {
    /**
     * The field that is annotated with @Unused may not be accessed via a
     * receiver that is annotated with the "when" annotation.
     */
    Class<? extends Annotation> when();
}
