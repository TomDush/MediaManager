package fr.dush.mediamanager.plugins.jmplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/** A thread that reads from an input stream and outputs to another line by line. */
public class LineRedirecter extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineRedirecter.class);

    /** The input stream to read from. */
    private InputStream in;
    /** The output stream to write to. */
    private OutputStream out;
    /** The prefix used to prefix the lines when outputting to the logger. */
    private String prefix;

    /**
     * @param in     the input stream to read from.
     * @param out    the output stream to write to.
     * @param prefix the prefix used to prefix the lines when outputting to the logger.
     */
    public LineRedirecter(InputStream in, OutputStream out, String prefix) {
        this.in = in;
        this.out = out;
        this.prefix = prefix;
    }

    public void run() {
        try {
            // creates the decorating reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintStream printStream = new PrintStream(out);
            String line;

            // read line by line
            while ((line = reader.readLine()) != null) {
                LOGGER.info((prefix != null ? prefix : "") + line);
                printStream.println(line);
            }
        } catch (IOException exc) {
            LOGGER.warn("An error has occured while grabbing lines", exc);
        }
    }
}
