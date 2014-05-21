package fr.dush.mediamanager.business.modules.impl;

import fr.dush.mediamanager.engine.SpringJUnitTest;

//@ApplicationScoped
//@Module(id = "junit-modulesmanagement", name = "Testing Module Management")
//@ToString(of = "instanceId")
public abstract class ModulesManagerImplTest extends SpringJUnitTest {

    // TODO Add tests if system if kept

    //	private static final Logger LOGGER = LoggerFactory.getLogger(ModulesManagerImplTest.class);
    //
    //	@Inject
    //	private IModulesManager modulesManagement;
    //
    //	private static int instanceCount = 0;
    //
    //	@Getter
    //	private int instanceId;
    //
    //	@PostConstruct
    //	public void modulesManagerImplTest() {
    //		LOGGER.debug("New instance of ModulesManagementImplTest");
    //		synchronized (ModulesManagerImplTest.class) {
    //			instanceId = instanceCount++;
    //		}
    //	}
    //
    //	@Test
    //	public void test_findModuleByType() throws Exception {
    //		final Collection<ModulesManagerImplTest> modules = modulesManagement.findModuleByType
    // (ModulesManagerImplTest.class);
    //		LOGGER.debug("{}", modules.toString());
    //
    //		final Collection<ModulesManagerImplTest> modules2 = modulesManagement.findModuleByType
    // (ModulesManagerImplTest.class);
    //		LOGGER.debug("{}", modules2.toString());
    //
    //		assertThat(extractProperty("instanceId").from(modules)).hasSize(1).contains(instanceId);
    //		assertThat(extractProperty("instanceId").from(modules2)).hasSize(1).contains(instanceId);
    //	}
    //
    //	@Test
    //	public void testFindModuleById() throws Exception {
    //		final CdiJunitTest module = modulesManagement.findModuleById(CdiJunitTest.class, "junit-modulesmanagement");
    //		assertThat(module).isNotNull();
    //	}

}
