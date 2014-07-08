package fr.dush.mediamanager.plugins.webui;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebUIModuleTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUIModuleTest.class);

    private final String USER_AGENT = "Mozilla/5.0";

    @InjectMocks
    private WebUIModule launcher;

    @Mock
    private ModuleConfiguration configuration;

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Test
//    @Ignore("Long test ...")
    public void testNewInstantiate() throws Exception {
        when(configuration.resolveProperties(anyString(), any(Properties.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String config = (String) invocation.getArguments()[0];
                return config.replaceAll("\\$\\{webui.port\\}", "8059")
                             .replaceAll("\\$\\{webui.subcontext\\}", "/")
                             .replaceAll("\\$\\{webui.resources\\}", "src/main/webapp/");
            }
        });
        launcher.startJetty();

        Thread.sleep(60000);
        assertThat(sendGet("http://localhost:8059/index_static.html")).isEqualTo(200);

        // Must stop application now...
        //        assertThat(sendGet("http://localhost:8080/")).isEqualTo(-1);
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
            LOGGER.info("Could not get page {}", url, e);
            return -1;
        }
    }
}
