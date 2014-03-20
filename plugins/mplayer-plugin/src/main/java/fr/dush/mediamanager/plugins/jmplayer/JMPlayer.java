package fr.dush.mediamanager.plugins.jmplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * A player which is actually an interface to the famous MPlayer.
 *
 * @author Adrian BER
 */
public class JMPlayer {

    private static Logger LOGGER = LoggerFactory.getLogger(JMPlayer.class.getName());

    /** The path to the MPlayer executable. */
    private String mplayerPath = "/usr/bin/mplayer";
    /** Options passed to MPlayer. */
    private String mplayerOptions = "-slave -idle";

    /** The process corresponding to MPlayer. */
    private Process mplayerProcess;
    /** The standard input for MPlayer where you can send commands. */
    private PrintStream mplayerIn;
    /** A combined reader for the the standard output and error of MPlayer. Used to read MPlayer responses. */
    private BufferedReader mplayerOutErr;

    public JMPlayer() {
    }

    /** @return the path to the MPlayer executable. */
    public String getMPlayerPath() {
        return mplayerPath;
    }

    /**
     * Sets the path to the MPlayer executable.
     *
     * @param mplayerPath the new MPlayer path; this will be actually efective after {@link #close() closing} the
     *                    currently running player.
     */
    public void setMPlayerPath(String mplayerPath) {
        this.mplayerPath = mplayerPath;
    }

    public void open(File file) throws IOException {
        String path = file.getAbsolutePath().replace('\\', '/');
        if (mplayerProcess == null) {
            // start MPlayer as an external process
            String command = mplayerPath + " " + mplayerOptions + " " + path ;
            LOGGER.info("Starting MPlayer process: " + command);
            mplayerProcess = Runtime.getRuntime().exec(command);

            // create the piped streams where to redirect the standard output and error of MPlayer
            // specify a bigger pipesize
            PipedInputStream readFrom = new PipedInputStream(1024 * 1024);
            PipedOutputStream writeTo = new PipedOutputStream(readFrom);
            mplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

            // create the threads to redirect the standard output and error of MPlayer
            new LineRedirecter(mplayerProcess.getInputStream(), writeTo, "MPlayer says: ").start();
            new LineRedirecter(mplayerProcess.getErrorStream(), writeTo, "MPlayer encountered an error: ").start();

            // the standard input of MPlayer
            mplayerIn = new PrintStream(mplayerProcess.getOutputStream());
        } else {
            execute("loadfile \"" + path + "\" 0");
        }
        // wait to start playing
        waitForAnswer("Starting playback...");
        LOGGER.info("Started playing file " + path);
    }

    public void close() {
        if (mplayerProcess != null) {
            execute("quit");
            try {
                mplayerProcess.waitFor();
            } catch (InterruptedException e) {
            }
            mplayerProcess = null;
        }
    }

    public File getPlayingFile() {
        String path = getProperty("path");
        return path == null ? null : new File(path);
    }

    public void togglePlay() {
        execute("pause");
    }

    public boolean isPlaying() {
        return mplayerProcess != null;
    }

    public long getTimePosition() {
        return getPropertyAsLong("time_pos");
    }

    public void setTimePosition(long seconds) {
        setProperty("time_pos", seconds);
    }

    public long getTotalTime() {
        return getPropertyAsLong("length");
    }

    public float getVolume() {
        return getPropertyAsFloat("volume");
    }

    public void setVolume(float volume) {
        setProperty("volume", volume);
    }

    protected String getProperty(String name) {
        if (name == null || mplayerProcess == null) {
            return null;
        }
        String s = "ANS_" + name + "=";
        String x = execute("get_property " + name, s);
        if (x == null) {
            return null;
        }
        if (!x.startsWith(s)) {
            return null;
        }
        return x.substring(s.length());
    }

    protected long getPropertyAsLong(String name) {
        try {
            return Long.parseLong(getProperty(name));
        } catch (NumberFormatException exc) {
        } catch (NullPointerException exc) {
        }
        return 0;
    }

    protected float getPropertyAsFloat(String name) {
        try {
            return Float.parseFloat(getProperty(name));
        } catch (NumberFormatException exc) {
        } catch (NullPointerException exc) {
        }
        return 0f;
    }

    protected void setProperty(String name, String value) {
        execute("set_property " + name + " " + value);
    }

    protected void setProperty(String name, long value) {
        execute("set_property " + name + " " + value);
    }

    protected void setProperty(String name, float value) {
        execute("set_property " + name + " " + value);
    }

    /**
     * Sends a command to MPlayer..
     *
     * @param command the command to be sent
     */
    private void execute(String command) {
        execute(command, null);
    }

    /**
     * Sends a command to MPlayer and waits for an answer.
     *
     * @param command  the command to be sent
     * @param expected the string with which has to start the line; if null don't wait for an answer
     * @return the MPlayer answer
     */
    private String execute(String command, String expected) {
        if (mplayerProcess != null) {
            LOGGER.info("Send to MPlayer the command \"" + command + "\" and expecting " +
                        (expected != null ? "\"" + expected + "\"" : "no answer"));
            mplayerIn.print(command);
            mplayerIn.print("\n");
            mplayerIn.flush();
            LOGGER.info("Command sent");
            if (expected != null) {
                String response = waitForAnswer(expected);
                LOGGER.info("MPlayer command response: " + response);
                return response;
            }
        }
        return null;
    }

    /**
     * Read from the MPlayer standard output and error a line that starts with the given parameter and return it.
     *
     * @param expected the expected starting string for the line
     * @return the entire line from the standard output or error of MPlayer
     */
    private String waitForAnswer(String expected) {
        // todo add the possibility to specify more options to be specified
        // todo use regexp matching instead of the beginning of a string
        String line = null;
        if (expected != null) {
            try {
                while ((line = mplayerOutErr.readLine()) != null) {
                    LOGGER.info("Reading line: " + line);
                    if (line.startsWith(expected)) {
                        return line;
                    }
                }
            } catch (IOException e) {
            }
        }
        return line;
    }

    public static void main(String[] args) throws IOException {
        JMPlayer jmPlayer = new JMPlayer();
        // open a video file
        jmPlayer.open(new File("video.avi"));
        // skip 2 minutes
        jmPlayer.setTimePosition(120);
        // set volume to 90%
        jmPlayer.setVolume(90);
    }
}