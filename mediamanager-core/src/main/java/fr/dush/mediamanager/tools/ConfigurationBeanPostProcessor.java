package fr.dush.mediamanager.tools;

import java.lang.reflect.Field;

import fr.dush.mediamanager.dao.configuration.IConfigurationDAO;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

import javax.inject.Named;

/** Inject ModuleConfiguration in Spring beans */
@Named
public class ConfigurationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBeanPostProcessor.class);

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(ModuleConfiguration.class)
                    && field.isAnnotationPresent(Configuration.class)) {

                LOGGER.warn("Should inject configuration into: {}", bean);
                IConfigurationDAO configurationDAO = getConfigurationDAO();
                LOGGER.warn("Will use: {}", configurationDAO);
            }
        }
        return bean;
    }

    private IConfigurationDAO getConfigurationDAO() {
        return applicationContext.getBean(IConfigurationDAO.class);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
