
package com.clockwork.network.serializing;

import com.clockwork.network.serializing.serializers.FieldSerializer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation when a class is going to be transferred
 *  over the network.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Serializable {
    Class serializer() default FieldSerializer.class;
    short id() default 0;
}
