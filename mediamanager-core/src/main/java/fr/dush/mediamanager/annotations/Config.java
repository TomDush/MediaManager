package fr.dush.mediamanager.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

/**
 * This annotation must be define to inject {@link ModuleConfiguration}s.
 *
 * <p>
 * There is 2 modes to use this annotation :
 * <ul>
 * <li>Standalone : annotation must define {@link #packageName()}, configuration ID.</li>
 * <li>From Module : <code>configId</code> is defined in {@link Module} annotation. This one is found implicitly on this class, on
 * explicitly on class {@link #entryPoint()}.
 * </ul>
 * </p>
 *
 *
 * @author Thomas Duchatelle
 *
 */
@Target({ PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /** File description identifier */
    String id();

	/** Module name, can be override by name in definition file. */
    @Deprecated
    String name() default "";

	/** Package's name : used as key to find appropriate {@link ModuleConfiguration}. */
    @Deprecated
    String packageName() default "";

	/** Module entry point, by default use class in which is injected configuration. */
    @Deprecated
    Class<?> entryPoint() default Config.class;

	/** URL to file containing meta information on each configuration parameter. This file must be in class path. */
    @Deprecated
    String definition() default "";

}
