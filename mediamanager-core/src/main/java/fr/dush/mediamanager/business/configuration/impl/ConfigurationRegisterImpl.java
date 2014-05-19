package fr.dush.mediamanager.business.configuration.impl;

import fr.dush.mediamanager.business.configuration.IConfigurationRegister;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.List;

import static com.google.common.collect.Lists.*;

@Named
public class ConfigurationRegisterImpl implements IConfigurationRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationRegisterImpl.class);

    private List<ModuleConfiguration> configurations = newArrayList();

    @Override
    public void registerConfiguration(ModuleConfiguration moduleConfiguration) {
        LOGGER.debug("Register {}", moduleConfiguration);
        configurations.add(moduleConfiguration);
    }

    @Override
    public List<ModuleConfiguration> findAll() {
        return newCopyOnWriteArrayList(configurations);
    }

}
