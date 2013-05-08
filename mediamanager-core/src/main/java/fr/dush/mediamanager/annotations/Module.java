package fr.dush.mediamanager.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * All entry point module must defined this annotation.
 *
 * <p>
 * The only mandatory field is {@link #name()}. This is to be displayed in UI.
 * </p>
 *
 * <p>
 * This annotation is used to configuration, see {@link Configuration}.
 * </p>
 *
 * @see Configuration
 * @author Thomas Duchatelle
 *
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface Module {

	/** Module name (to be displayed) */
	String name();

	/** Module description (to be displayed, can be HTML) */
	String description() default "";

	/** Override package name, default class package. */
	String packageName() default "";

}
