package fr.dush.mediamanager.annotations;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * This annotation must be define to inject {@link ModuleConfiguration}s.
 *
 * @author Thomas Duchatelle
 */
@Target({PARAMETER, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /** Config identifier. It's also the description file name. */
    String id();

}
