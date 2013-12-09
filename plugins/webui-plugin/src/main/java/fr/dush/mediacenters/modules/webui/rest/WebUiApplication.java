package fr.dush.mediacenters.modules.webui.rest;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Find in loaded classpath Providers and Resources.
 *
 * <p>
 *     RestEasy scanner search resources and providers from WEB-INF/classes and libs. But, this paths are not deploy in MediaManager.
 * </p>
 *
 * @author Thomas Duchatelle
 */
@ApplicationScoped
public class WebUiApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUiApplication.class);

    private Set<Class<?>> providers = new HashSet<>();

    private Set<Class<?>> resources = new HashSet<>();

    @PostConstruct
    public void findMatchingClasses() {
        LOGGER.debug("--> findMatchingClasses");
        try {
            scanPackage("fr.dush");
            scanPackage("com.fasterxml");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Can't scan classes", e);
        }
    }

    private void scanPackage(String packageName) throws IOException, ClassNotFoundException {
        ImmutableSet<ClassPath.ClassInfo> classes = ClassPath.from(WebUiApplication.class.getClassLoader())
                                                             .getTopLevelClassesRecursive(packageName);
        for (ClassPath.ClassInfo info : classes) {
            Class<?> clazz = Class.forName(info.getName());
            if (clazz.isAnnotationPresent(Provider.class)) {
                LOGGER.debug("found provider {}", clazz.getName());
                providers.add(clazz);
            }
            if (clazz.isAnnotationPresent(Path.class)) {
                LOGGER.debug("found resources {}", clazz.getName());
                resources.add(clazz);
            }
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(Iterables.concat(providers, resources));
    }
}
