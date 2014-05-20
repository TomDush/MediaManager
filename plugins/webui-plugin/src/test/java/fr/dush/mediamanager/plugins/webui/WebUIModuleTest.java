package fr.dush.mediamanager.plugins.webui;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

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

        }
        catch (ConnectException e) {
            return -1;
        }
    }
}
