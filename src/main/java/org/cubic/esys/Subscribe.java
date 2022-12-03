package org.cubic.esys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    int priority() default 0;

    /**
     * This is only relevant for EventHooks
     * to determine the type class of the event
     * it is listening for. This will be ignored
     * in all other cases.
     */
    Class<?> type() default Dummy.class;

    String name() default "";
}
