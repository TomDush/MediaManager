package fr.dush.mediamanager.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Beans which carries this annotation will be initialized at application startup.
 *
 * @author Thomas Duchatelle
 *
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Startup {

	/** Force StartupExtension to use another class : resolve nonproxifiable issue */
	Class<?> superclass() default Startup.class;

}
