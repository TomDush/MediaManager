package fr.dush.mediacenters.modules.webui.tools;

import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Thomas Duchatelle
 */
public class DozerMapperFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DozerMapperFactory.class);

    @Produces
    @ApplicationScoped
    public Mapper getDozerMapper() {

        DozerBeanMapper mapper = new DozerBeanMapper();

        try {
            for (String mappingFile : ResourceList.getResources(Pattern.compile(".*dozer/.*mappers\\.xml"))) {
                LOGGER.debug("[DOZER] Load mapping file: {}", mappingFile);
                mapper.addMapping(new FileInputStream(mappingFile));
                // DozerMapperFactory.class.getClassLoader().getResourceAsStream()
            }
        } catch (IOException e) {
            LOGGER.warn("Couldn't load Dozer mapping files: {}.", e.getMessage(), e);
            throw new ConfigurationException("Couldn't load Dozer files.", e);
        }

        return mapper;
    }
}
