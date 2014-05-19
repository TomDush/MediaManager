package fr.dush.mediamanager.tools;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import org.bson.types.ObjectId;
import org.dozer.BeanFactory;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.filter;

/**
 * @author Thomas Duchatelle
 */
@Configuration
public class DozerMapperFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DozerMapperFactory.class);

    @Bean
    public Mapper getDozerMapper() {

        DozerBeanMapper mapper = new DozerBeanMapper();

        // Find mapping file in classpath
        try {
            final Pattern pattern = Pattern.compile(".*dozer/.*mappers\\.xml");
            ImmutableSet<ClassPath.ResourceInfo> allResources =
                    ClassPath.from(DozerMapperFactory.class.getClassLoader()).getResources();

            Collection<String> resources =
                    newArrayList(transform(filter(allResources, new Predicate<ClassPath.ResourceInfo>() {
                        @Override
                        public boolean apply(ClassPath.ResourceInfo input) {
                            return pattern.matcher(input.getResourceName()).matches();
                        }
                    }), new Function<ClassPath.ResourceInfo, String>() {
                        @Override
                        public String apply(ClassPath.ResourceInfo input) {
                            return input.getResourceName();
                        }
                    }));

            LOGGER.info("[DOZER] Load mapping files: {}", resources, allResources);

            for (String mappingFile : resources) {
                mapper.addMapping(DozerMapperFactory.class.getClassLoader().getResourceAsStream(mappingFile));
            }
        } catch (IOException e) {
            LOGGER.warn("Couldn't load Dozer mapping files: {}.", e.getMessage(), e);
            throw new ConfigurationException("Couldn't load Dozer files.", e);
        }

        // Register custom converters
        //        mapper.setCustomConverters(newArrayList(new ObjectIdConverter()));
        //        mapper.setFactories();

        return mapper;
    }

    public static class ObjectIdFactory implements BeanFactory {

        @Override
        public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
            if (source instanceof String) {
                return new ObjectId((String) source);
            }
            return null;
        }
    }

    public static class StringFactory implements BeanFactory {

        @Override
        public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
            return source == null ? null : source.toString();
        }
    }

    public static class ObjectIdConverter extends DozerConverter<ObjectId, ObjectId> {

        public ObjectIdConverter() {
            super(ObjectId.class, ObjectId.class);
        }

        @Override
        public ObjectId convertTo(ObjectId source, ObjectId destination) {
            return new ObjectId(source.toString());
        }

        @Override
        public ObjectId convertFrom(ObjectId source, ObjectId destination) {
            return new ObjectId(source.toString());
        }
    }
}
