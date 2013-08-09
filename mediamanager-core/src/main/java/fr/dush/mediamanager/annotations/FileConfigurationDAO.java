package fr.dush.mediamanager.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;

/**
 * There is 2 {@link IConfigurationDAO}'s implementations : by file (properties file on hard drive), and by database ; this annotation
 * describe the one by file which don't need database connection.
 *
 * @author Thomas Duchatelle
 *
 */
@Target({ TYPE, FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface FileConfigurationDAO {

}
