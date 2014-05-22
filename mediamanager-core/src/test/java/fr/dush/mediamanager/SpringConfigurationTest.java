package fr.dush.mediamanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/**
 * @author Thomas Duchatelle
 */
public class SpringConfigurationTest extends SimpleJunitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigurationTest.class);

    @Test
    public void testMapper() throws Exception {
        FieldSet fieldSet = new FieldSet();
        fieldSet.addValue("key", "value", false);

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, fieldSet);

        LOGGER.info("JSON value: {}", writer);

    }
}
