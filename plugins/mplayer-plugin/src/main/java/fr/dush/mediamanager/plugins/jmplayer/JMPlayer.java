package fr.dush.mediamanager.plugins.jmplayer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * A player which is actually an interface to the famous MPlayer.
 *
 * @author Adrian BER
 * @author Thomas Duchatelle
 */
public class JMPlayer implements OutputListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMPlayer.class.getName());

    public static final String QUIT_COMMAND = "quit";
    public static final String PAUSE_COMMAND = "pause";

    public static final String REQUIRED_OPTION = "-slave";

    public static final Pattern STATUS_LINE_PATTERN = Pattern.compile("A:\\s*([\\d\\.]+).*");
    public static final Pattern PARAMETER_PATTERN = Pattern.compile("ANS_(\\w+)=(.*)");
    public static final int PROPERTY_TIMEOUT = 1000;

    private Event<PlayerEvent> onPlayerEvent;

    /** The path to the MPlayer executable. */
    private final String mplayerPath;
    /** Options passed to MPlayer. */
    private final String mplayerOptions;

    private List<Path> medias;

    /** The process corresponding to MPlayer. */
    private Process mplayerProcess;
    /** The standard input for MPlayer where you can send commands. */
    private PrintStream mplayerIn;

    private Double position;
    private Long totalLength = null;
    @Getter
    private boolean paused;

    @Getter(AccessLevel.PACKAGE)
    private Set<ArgReader> argReaders = Collections.synchronizedSet(new HashSet<ArgReader>());
    private long statusHits = 0;

    public JMPlayer(Event<PlayerEvent> onPlayerEvent, String mplayerPath, String mplayerOptions) {
        this.onPlayerEvent = onPlayerEvent;
        this.mplayerPath = mplayerPath;
        this.mplayerOptions = REQUIRED_OPTION + " " + mplayerOptions;
    }

    /** Start playing 1 file */
    public void play(Path path) throws IOException {
        ArrayList paths = new ArrayList();
        paths.add(path);
        play(paths);
    }

    /** Start file to read together as 1 bigger file */
    public void play(List<Path> paths) throws IOException {
        if (mplayerProcess != null) {
            throw new IllegalStateException("Player already running, could not start reading another file.");
        }

        medias = paths;

        // Start MPlayer as an external process
        String filePaths = Joiner.on(" ").join(transform(paths, new Function<Path, Object>() {
            @Override
            public Object apply(Path input) {
                return input.toAbsolutePath();
            }
        }));

        String command = mplayerPath + " " + mplayerOptions + " " + filePaths;
        LOGGER.info("Starting MPlayer process: " + command);
        mplayerProcess = Runtime.getRuntime().exec(command);

        // create the threads to redirect the standard output and error of MPlayer
        new OutputReader(Level.INFO, this, mplayerProcess.getInputStream()).start();
        new OutputReader(Level.WARN, this, mplayerProcess.getErrorStream()).start();

        // the standard input of MPlayer
        mplayerIn = new PrintStream(mplayerProcess.getOutputStream());

        // Fire event to prevent start reading
        fireEvent(PlayerEvent.START);
    }

    /** Quit and return position */
    public long quit() throws InterruptedException {
        if (mplayerProcess != null) {
            execute(QUIT_COMMAND);

            mplayerProcess.waitFor();
            mplayerProcess = null;
        }

        return position.longValue();
    }

    /** Toggle pause/play, return current position */
    public long pause() {
        execute(PAUSE_COMMAND);

        return position.longValue();
    }

    /** Return if mplayer instance is still alive, return true even if it's paused. */
    public boolean isPlaying() {
        return mplayerProcess != null;
    }

    public long getPosition() {
        if (position != null) {
            return position.longValue();
        }

        try {
            return (long) Double.parseDouble(getProperty("stream_time_pos"));
        } catch (Exception e) {
            return -1;
        }
    }

    public void setPosition(long position) {
        execute("set time_pos " + position);
    }

    public long getTotalLength() {
        if (totalLength == null) {
            String value = getProperty("length");
            if (isNotEmpty(value)) {
                totalLength = (long) Double.parseDouble(value);
            }
        }

        return totalLength == null ? 0 : totalLength;
    }

    /** Read a property */
    protected String getProperty(final String name) {
        if (name == null || mplayerProcess == null) {
            return null;
        }

        // Append a listener
        ArgReader reader = new ArgReader(name);
        argReaders.add(reader);

        // Execute command
        try {
            synchronized (reader) {
                execute("get_property " + name);
                reader.wait(PROPERTY_TIMEOUT);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Process has been interrupted...", e);
        }

        return reader.getValue();
    }

    /**
     * Sends a command to MPlayer..
     *
     * @param command the command to be sent
     */
    protected void execute(String command) {
        if (mplayerProcess == null) {
            throw new IllegalArgumentException("Player isn't started...");
        }

        mplayerIn.print(command);
        mplayerIn.print("\n");
        mplayerIn.flush();
    }

    private void fireEvent(int type) {
        onPlayerEvent.fire(new PlayerEvent(this, type, getPosition(), getTotalLength(), medias));
    }

    /** DO NOT USE: internal purpose method to read events from MPlayer */
    @Override
    public void readMPlayerLog(Level level, String line) {
        if (level == Level.WARN) {
            LOGGER.warn("[MPlayer] {}", line);
        } else if (level == Level.QUIT && mplayerProcess != null) {
            mplayerProcess = null;
            fireEvent(PlayerEvent.FINISHED);
        }

        if (line.startsWith("A:")) {
            if (paused) {
                fireEvent(PlayerEvent.PLAY);
            }

            // Status line (lecture in progress)
            paused = false;
            Matcher matcher = STATUS_LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                position = Double.valueOf(matcher.group(1));

                if (statusHits++ % 20 == 0) {
                    fireEvent(PlayerEvent.POSITION);
                }
            }

        } else if (line.startsWith("ANS_")) {
            // Response to a parameter
            Matcher matcher = PARAMETER_PATTERN.matcher(line);
            if (matcher.matches()) {
                for (ArgReader r : argReaders) {
                    r.doIt(matcher.group(1), matcher.group(2));
                }
            }

        } else if (line.contains("PAUSE")) {
            // Is paused
            paused = true;

            fireEvent(PlayerEvent.PAUSE);
        }

    }

}