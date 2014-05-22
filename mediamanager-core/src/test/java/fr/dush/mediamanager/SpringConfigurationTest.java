package fr.dush.mediamanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import org.junit.Test;

/**
 * @author Thomas Duchatelle
 */
public class SpringConfigurationTest {

    @Test
    public void testMapper() throws Exception {
        FieldSet fieldSet = new FieldSet();
        fieldSet.addValue("key", "value", false);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(System.out, fieldSet);
        //        FieldSet fieldSet = mapper.readValue(resource.getInputStream(), FieldSet.class);

    }
}
