package fr.dush.mediamanager.business.configuration.impl;

import static com.google.common.collect.Lists.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.configuration.IConfigurationRegister;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

@ApplicationScoped
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
