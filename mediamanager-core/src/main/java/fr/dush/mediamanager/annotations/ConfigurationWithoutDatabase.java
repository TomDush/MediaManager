package fr.dush.mediamanager.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * If user preference for this module isn't in database, but in properties file.
 *
 * <p>
 * For example, database parameters can't be saved in database ! Thus, there placed in properties file.
 * </p>
 */
@Target({ TYPE, FIELD, PARAMETER, METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface ConfigurationWithoutDatabase {

}
