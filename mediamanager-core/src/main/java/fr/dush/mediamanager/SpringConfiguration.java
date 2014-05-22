package fr.dush.mediamanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.FieldSet;

@Configuration
@ComponentScan(basePackages = "fr.dush.mediamanager")
public class SpringConfiguration {

    @Bean
    public ModuleConfiguration getMockModuleConfiguration() {
        return new ModuleConfiguration(null, new FieldSet());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] { new ClassPathResource("configuration/tempconfig.properties") };
        p.setLocations(resourceLocations);

        return p;
    }
}
