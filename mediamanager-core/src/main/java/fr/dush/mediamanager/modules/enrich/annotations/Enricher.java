package fr.dush.mediamanager.modules.enrich.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Define module's bean which can enrich media's meta-data.
 *
 * @author Thomas Duchatelle
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({  ElementType.TYPE, })
public @interface Enricher {

	/** Module name */
	String name();

	/** Modeule description */
	String description();

//	/** Mediatype which can be Compatible with this */
//	Class<? extends Media>[] compatilities() default Media.class;
}
