package io.vertx.ext.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * # Handler
 * <p>
 * Annotate a method that might block the event loop while handling the request.
 * E.g: Calling a legacy blocking API or doing some intensive calculation.
 *
 * A blocking handler looks just like a normal handler but it’s called by Vert.x
 * using a thread from the worker pool not using an event loop.
 *
 * @author <a href="mailto:jairo.tylera@gmail.com">Jairo Tylera</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Handler {
    boolean blocking() default false;
}
