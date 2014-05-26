package fr.dush.mediamanager.engine;

import com.google.common.collect.Lists;
import fr.dush.mediamanager.engine.mongodb.DatabaseScript;
import fr.dush.mediamanager.engine.mongodb.DatabaseScripts;
import fr.dush.mediamanager.engine.mongodb.MongoDBDatasetManager;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class MongoDBJunitClassRunner extends SpringJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBJunitClassRunner.class);

    public MongoDBJunitClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        List<DatabaseScript> scripts = newArrayList();
        boolean inherits = true;

        // Read method annotations...
        inherits = appendDatabaseScript(scripts, method.getAnnotation(DatabaseScript.class));
        inherits = appendDatabaseScripts(scripts, inherits, method.getAnnotation(DatabaseScripts.class));

        // Read class annotations...
        Class<?> testClass = test.getClass();
        while (inherits && null != testClass && !Object.class.equals(testClass)) {
            // Append annotations
            inherits = appendDatabaseScript(scripts, testClass.getAnnotation(DatabaseScript.class));
            inherits = appendDatabaseScripts(scripts, inherits, testClass.getAnnotation(DatabaseScripts.class));

            testClass = testClass.getSuperclass();
        }

        // Initialize data set...
        LOGGER.debug("Initialize with scrips : {}", scripts);
        MongoJunitTest junitTest = null;
        if (test instanceof MongoJunitTest) {
            junitTest = (MongoJunitTest) test;
        } else if (!scripts.isEmpty()) {
            throw new RuntimeException(
                    "Test class should inherit from MongoJunitTest to use @DatabaseScripts annotation");
        }
        for (DatabaseScript script : Lists.reverse(scripts)) {
            MongoDBDatasetManager datasetManager = junitTest.getApplicationContext().getBean(MongoDBDatasetManager
                                                                                                     .class);
            datasetManager.initializeDataset(script);
        }

        // Method execution...
        return super.methodInvoker(method, test);
    }

    private boolean appendDatabaseScripts(List<DatabaseScript> scripts, boolean inherits,
                                          final DatabaseScripts annotation2) {
        if (inherits && null != annotation2) {
            scripts.addAll(Arrays.asList(annotation2.value()));
            inherits = annotation2.inherits();
        }
        return inherits;
    }

    private boolean appendDatabaseScript(List<DatabaseScript> scripts, final DatabaseScript script) {
        if (null != script) {
            scripts.add(script);
            return script.inherits();
        }

        return true;
    }

}
