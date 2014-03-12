package fr.dush.mediacenters.modules.webui.tools;

import org.junit.Test;

import java.util.Collection;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Thomas Duchatelle
 */
public class ResourceListTest {

    @Test
    public void testGetResources() throws Exception {
        Collection<String> resources = ResourceList.getResources(Pattern.compile(".*dozer/.*mappers\\.xml"));
        assertThat(resources).isNotEmpty();
    }
}
