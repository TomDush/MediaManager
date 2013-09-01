package fr.dush.mediamanager.business.configuration;

import java.util.List;

public interface IConfigurationRegister {

	void registerConfiguration(ModuleConfiguration moduleConfiguration);

	List<ModuleConfiguration> findAll();
}
