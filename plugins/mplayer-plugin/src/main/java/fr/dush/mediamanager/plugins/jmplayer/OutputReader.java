package fr.dush.mediamanager.plugins.jmplayer;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@AllArgsConstructor
public class OutputReader extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputReader.class);

    private Level level;
    private OutputListener outputListener;

    /** The input stream to read from. */
    private InputStream in;

    public void run() {
        try {
            // creates the decorating reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    outputListener.readMPlayerLog(level, line);
                } catch (Exception e) {
                    LOGGER.warn("outputListener throw an exception: {}", e.getMessage(), e);
                }
            }

            outputListener.readMPlayerLog(Level.QUIT, "EOF");

        } catch (IOException e) {
            LOGGER.warn("An error has occurred while grabbing lines", e);
        }
    }
}
