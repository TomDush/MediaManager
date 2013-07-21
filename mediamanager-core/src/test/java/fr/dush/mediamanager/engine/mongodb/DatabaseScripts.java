package fr.dush.mediamanager.engine.mongodb;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Multiple {@link DatabaseScript} on method or class.
 *
 * @author Thomas Duchatelle
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {METHOD, TYPE})
public @interface DatabaseScripts {

	/** Multiple {@link DatabaseScript}. {@link DatabaseScript#inherits()} won't be read. */
	DatabaseScript[] value();

	/** Inherits class and parent classes annotations, default true. */
	boolean inherits() default true;
}
