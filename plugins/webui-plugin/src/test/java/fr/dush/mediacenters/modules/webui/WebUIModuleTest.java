package fr.dush.mediacenters.modules.webui;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediamanager.tools.CDIUtils;

public class WebUIModuleTest {

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Test
	@Ignore("Use mvn jetty:run to test application.")
	public void testStartingServer() throws Exception {

		System.setProperty("mediamanager.propertiesfile", "../../mediamanager-core/src/test/resources/dbconfig-int.properties");

		CDIUtils.bootCdiContainer();
		try {
			WebUIModule launcher = CDIUtils.getBean(WebUIModule.class);
			launcher.startServer();

			launcher.join();
			// Thread.sleep(30000);

		} finally {
			CDIUtils.stopCdiContainer();
		}
	}
}
