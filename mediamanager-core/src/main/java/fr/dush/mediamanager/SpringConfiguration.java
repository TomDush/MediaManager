package fr.dush.mediamanager;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "fr.dush.mediamanager")
public class SpringConfiguration {

    @Bean
    public ModuleConfiguration getMockModuleConfiguration() {
        return new ModuleConfiguration(null, new FieldSet());
    }
}
