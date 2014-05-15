package fr.dush.mediamanager.plugins.webui.rest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Thomas Duchatelle
 */
public class WebUiApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUiApplicationTest.class);

    @Test
    public void testFindMatchingClasses() throws Exception {
        WebUiApplication webUiApplication = new WebUiApplication();
        webUiApplication.findMatchingClasses();

        ArrayList<Class<?>> classes = newArrayList(webUiApplication.getClasses());
        LOGGER.debug("Loaded classes: {}", classes);
        assertThat(classes).isNotEmpty();
    }
}
