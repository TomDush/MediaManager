package fr.dush.mediamanager.business.modules.impl;

import static org.fest.assertions.api.Assertions.*;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.engine.CdiJunitTest;

@ApplicationScoped
@Module(id = "junit-modulesmanagement", name = "Testing Module Management")
public class ModulesManagerImplTest extends CdiJunitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModulesManagerImplTest.class);

	@Inject
	private IModulesManager modulesManagement;

	public ModulesManagerImplTest() {
		LOGGER.debug("New instance of ModulesManagementImplTest");
	}

	@Test
	public void test_findModuleByType() throws Exception {
		final Collection<ModulesManagerImplTest> modules = modulesManagement.findModuleByType(ModulesManagerImplTest.class);
		LOGGER.debug("{}", modules.toString());

		final Collection<ModulesManagerImplTest> modules2 = modulesManagement.findModuleByType(ModulesManagerImplTest.class);
		LOGGER.debug("{}", modules2.toString());

		assertThat(modules).isNotEmpty().contains(this);
		assertThat(modules2).isNotEmpty().contains(this);
	}

	@Test
	public void testFindModuleById() throws Exception {
		final CdiJunitTest module = modulesManagement.findModuleById(CdiJunitTest.class, "junit-modulesmanagement");
		assertThat(module).isNotNull();
	}
}
