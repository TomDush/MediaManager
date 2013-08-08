package fr.dush.mediamanager.engine.mongodb;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare how to load a <code>MongoDB</code> collection.
 *
 * @author Thomas Duchatelle
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { METHOD, TYPE })
public @interface DatabaseScript {

	/** Entity class's collection to load. Read morphia's {@link Entity} annotation to get collection's name. */
	Class<?> clazz();

	/** Force collection's name */
	String collectionName() default "";

	/** Locations to JSON files to load */
	String[] locations() default {};

	/** Clear database before loading it, default is true */
	boolean clear() default true;

	/** Inherits class and parent classes annotations, default true. */
	boolean inherits() default true;
}
