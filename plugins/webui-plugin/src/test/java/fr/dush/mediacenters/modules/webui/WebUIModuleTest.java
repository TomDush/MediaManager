package fr.dush.mediacenters.modules.webui;

import static org.fest.assertions.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.dush.mediacenters.modules.webui.controllers.ProviderData;
import fr.dush.mediamanager.tools.CDIUtils;

public class WebUIModuleTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModuleTest.class);

	private final String USER_AGENT = "Mozilla/5.0";

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Test
	@Ignore("Long test ...")
	public void testNewInstanciate() throws Exception {
		WebUIModule launcher = new WebUIModule();
		launcher.beforeStartCdi(null);

		assertThat(sendGet("http://localhost:8080/")).isEqualTo(200);

		final CDI<Object> weld = CDI.current();
		final BeanManager beanManager = weld.getBeanManager();
		LOGGER.info("Bean manager found : {}", beanManager);

		CDIUtils.bootCdiContainer();
		final ProviderData providerData = CDIUtils.getBean(beanManager, ProviderData.class);
		LOGGER.info("Provider data : {}", providerData);

		CDIUtils.stopCdiContainer();
		launcher.afterStopCdi();

		assertThat(sendGet("http://localhost:8080/")).isEqualTo(-1);
	}

	private int sendGet(String url) throws Exception {

		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		// Read content...
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine.trim());
			}
			in.close();

			// print result
			LOGGER.info("Response : {}", response);

			return con.getResponseCode();

		} catch (ConnectException e) {
			return -1;
		}
	}
}
