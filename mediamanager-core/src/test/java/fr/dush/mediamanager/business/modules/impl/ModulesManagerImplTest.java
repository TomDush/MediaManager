package fr.dush.mediamanager.business.modules.impl;

import static org.fest.assertions.api.Assertions.*;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.ToString;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.engine.CdiJunitTest;

@ApplicationScoped
@Module(id = "junit-modulesmanagement", name = "Testing Module Management")
@ToString(of = "instanceId")
public class ModulesManagerImplTest extends CdiJunitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModulesManagerImplTest.class);

	@Inject
	private IModulesManager modulesManagement;

	private static int instanceCount = 0;

	@Getter
	private int instanceId;

	@PostConstruct
	public void modulesManagerImplTest() {
		LOGGER.debug("New instance of ModulesManagementImplTest");
		synchronized (ModulesManagerImplTest.class) {
			instanceId = instanceCount++;
		}
	}

	@Test
	public void test_findModuleByType() throws Exception {
		final Collection<ModulesManagerImplTest> modules = modulesManagement.findModuleByType(ModulesManagerImplTest.class);
		LOGGER.debug("{}", modules.toString());

		final Collection<ModulesManagerImplTest> modules2 = modulesManagement.findModuleByType(ModulesManagerImplTest.class);
		LOGGER.debug("{}", modules2.toString());

		assertThat(extractProperty("instanceId").from(modules)).hasSize(1).contains(instanceId);
		assertThat(extractProperty("instanceId").from(modules2)).hasSize(1).contains(instanceId);
	}

	@Test
	public void testFindModuleById() throws Exception {
		final CdiJunitTest module = modulesManagement.findModuleById(CdiJunitTest.class, "junit-modulesmanagement");
		assertThat(module).isNotNull();
	}

}
