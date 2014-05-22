package fr.dush.mediamanager.annotations;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * All entry point module must defined this annotation. <p/> <p> The only mandatory field is {@link #name()}. This is to
 * be displayed in UI. </p> <p/> <p> This annotation is used to configuration, see {@link Config}. </p>
 *
 * @author Thomas Duchatelle
 * @see Config
 */
@Target({TYPE})
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

    /** Module ID used to find entry point */
    String id();

}
