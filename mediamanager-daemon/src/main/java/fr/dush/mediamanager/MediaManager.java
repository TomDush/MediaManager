package fr.dush.mediamanager;

import com.google.common.io.Files;
import fr.dush.mediamanager.config.ClientConfiguration;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.launcher.ContextLauncher;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import fr.dush.mediamanager.remote.impl.StoppedRemoteInterface;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Media center main class (containing mathod <code>main</code>
 *
 * @author Thomas Duchatelle
 */
public class MediaManager {

    private static final Logger LOGGER;

    private static final int DEFAULT_WIDTH = 800;

    /** Command line options (parser) */
    private static final Options OPTIONS = buildOptions();

    /** Configuration if none is defined. */
    private static final Path DEFAULT_CONFIG_PATH =
            Paths.get(System.getProperty("user.home"), ".mediamanager", "mediamanager.properties");

    private final CommandLine args;

    private final ClientConfiguration clientConfig;

    private MediaManagerRMI remoteInterface;

    /** Key in system directory defining where is the install dir. */
    public static final String INSTALL_DIR_KEY = "mediamanager.install";

    static {
        // Initialise LOG4J parameters: directory where Medima binaries are
        if (isEmpty(System.getProperty(INSTALL_DIR_KEY)) && isEmpty(System.getenv(INSTALL_DIR_KEY))) {
            String classPath = ContextLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (classPath.contains(":")) {
                classPath = classPath.substring(classPath.indexOf(":") + 1, classPath.length());
            }
            // Parent is directory where JAR is (bin directory), parent of this one is install dir.
            System.setProperty(INSTALL_DIR_KEY,
                               Paths.get(classPath).getParent().getParent().toAbsolutePath().toString());
        }

        // Only after, create logger
        LOGGER = LoggerFactory.getLogger(MediaManager.class);
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new GnuParser();

        try {
            final CommandLine parsed = parser.parse(OPTIONS, args);

            // "Meta" commands
            if (parsed.hasOption('h')) {
                printHelp(System.out);

            } else if (parsed.hasOption('v')) {
                printVersion();

            } else {
                final MediaManager mediaManager = new MediaManager(parsed);
                mediaManager.execute();

            }

            System.exit(0);

        } catch (ParseException e) {
            exitWithError(true, "Can't parse arguments : %s\n\n", e.getMessage());
        }
    }

    private static void exitWithError(boolean desplayHelp, String pattern, Object... args) {
        if (isNotEmpty(pattern)) {
            System.err.println(String.format(pattern, args));
        }

        if (desplayHelp) {
            printHelp(System.err);
        }
        System.exit(1);
    }

    private static void printVersion() {
        System.out.println("MediaManager version : " + MediaManager.class.getPackage().getImplementationVersion());
    }

    private static void printHelp(PrintStream stream) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(DEFAULT_WIDTH);
        formatter.printHelp("java -jar MediaManager.jar <options>", OPTIONS);

        System.out.println("\nWithout option, start daemon if not started, else display status.");

    }

    @SuppressWarnings("static-access")
    private static Options buildOptions() {
        Options options = new Options();

        // Informations
        options.addOption("h", "help", false, "print this help");
        options.addOption("v", "version", false, "print version");

        // Configuration
        options.addOption("p", "port", true, "port used to communicate with daemon.");
        options.addOption(OptionBuilder.withDescription("configuration file, default is " + DEFAULT_CONFIG_PATH)
                                       .hasArg()
                                       .withLongOpt("config")
                                       .withArgName("config file")
                                       .create("c"));

        // Control daemon
        options.addOption("s", "start", false, "start daemon, if it isn't started");
        options.addOption(OptionBuilder.withDescription("stop daemon, if it started").withLongOpt("stop").create());
        options.addOption(OptionBuilder.withDescription("get daemon status, and scanning progression if any")
                                       .withLongOpt("status")
                                       .create());

        options.addOption(OptionBuilder.withDescription(
                "scan given directory with defined scanner, server must be started. Can be used with --enricher")
                                       .hasArgs(2)
                                       .withArgName("media type> <directoryPath")
                                       .withLongOpt("scan")
                                       .create());
        options.addOption(OptionBuilder.withDescription("Used with --scan : define default enricher.")
                                       .hasArgs(1)
                                       .withArgName("enricher")
                                       .withLongOpt("enricher")
                                       .create());
        options.addOption(OptionBuilder.withDescription("show current configuration").withLongOpt("show").create());
        options.addOption(OptionBuilder.withDescription("set variable")
                                       .hasArgs(3)
                                       .withArgName("package> <name> <value")
                                       .withLongOpt("set")
                                       .create());

        return options;
    }

    /**
     * Launcher constructor to interpret and execute args.
     */
    public MediaManager(CommandLine args) {
        this.args = args;

        // Resolve config file
        Path configFile = DEFAULT_CONFIG_PATH;
        if (args.hasOption('c')) {
            configFile = Paths.get(args.getOptionValue('c'));
            if (!configFile.toFile().exists()) {
                throw new ConfigurationException("Configuration file %s doesn't exist.", configFile);
            }
        }

        // Create configuration if it doesn't exist
        createConfigurationFile(configFile);

        clientConfig =
                new ClientConfiguration(configFile, args.hasOption('p') ? castPort(args.getOptionValue('p')) : null);
    }

    /** Create configuration file with some comments. */
    private static void createConfigurationFile(Path configFile) {
        try {
            File config = configFile.toFile();
            if (!config.exists()) {
                config.createNewFile();

                Files.write("# Medima configuration file\n# Use medima.sh --help to see what options are available.\n",
                            config,
                            Charset.forName("UTF-8"));
            }

        } catch (IOException e) {
            LOGGER.warn("Could not create config file.", e);
        }
    }

    public void execute() {
        final boolean controlCommandExecuted = controlDaemon();

        if (!controlCommandExecuted) {
            executeComplexeCommand();
        }
    }

    private boolean controlDaemon() {
        boolean executed = true;
        if (args.hasOption("start")) {
            final boolean started = start();

            if (!started) {
                if (args.hasOption("start")) {
                    exitWithError(false, "Can't start daemon because is already started.");
                }

                printStatus();
            }

        } else if (args.hasOption("status")) {
            printStatus();

        } else if (args.hasOption("stop")) {
            stopDaemon();

        } else {
            executed = false;
        }

        return executed;
    }

    /**
     * Execute complex command : scan, set configuration by command line, ...
     */
    private void executeComplexeCommand() {
        if (args.hasOption("scan")) {
            // Scanning process
            final String[] arguments = args.getOptionValues("scan");
            try {
                getRemoteInterface().scan(MediaType.valueOfMediaType(arguments[0]),
                                          toAbsolute(arguments[1]),
                                          args.getOptionValue("enricher"));
                System.out.println("Scanning in progress...");

            } catch (RemoteException | IllegalArgumentException e) {
                exitWithError(false, e.getMessage());
            }

        } else if (args.hasOption("show")) {
            showConfig();

        } else if (args.hasOption("set")) {
            exitWithError(false, "Not yet implemented");
        }
    }

    private String toAbsolute(final String p) {
        return Paths.get(p).toAbsolutePath().normalize().toString();
    }

    private void showConfig() {
        try {
            final List<ConfigurationField> configs = getRemoteInterface().getFullConfiguration();
            if (configs.isEmpty()) {
                if (getRemoteInterface().getStatus() == Status.STOPPED) {
                    System.out.println("Server is stopped.");
                } else {
                    System.out.println("No configuration loaded...");
                }

                return;
            }

            System.out.println("Configuration : ");
            for (ConfigurationField f : configs) {
                System.out.println(String.format("\t- %-40s = %s %s",
                                                 f.getKey(),
                                                 f.getValue(),
                                                 f.isDefaultValue() ? "(default)" : ""));
                if (isNotEmpty(f.getDescription())) {
                    System.out.println("\t\t\t(" + f.getDescription() + ")");
                }
            }

        } catch (RemoteException e) {
            exitWithError(false, "Can't display configuration : " + e.getMessage());
        }
    }

    private void stopDaemon() {
        final Status st = getRemoteStatus();

        if (st != Status.STOPPED) {
            try {
                System.out.println("Stopping daemon...");
                getRemoteInterface().stop();

            } catch (RemoteException e) {
                exitWithError(false, "Can't stop daemon : %s", e.getMessage());
            }

        } else {
            System.out.println("Daemon is already stopped.");
        }

    }

    private Status getRemoteStatus() {
        try {
            return getRemoteInterface().getStatus();

        } catch (RemoteException e) {
            LOGGER.info("Remote server don't respond : {}", e.getMessage());
            return Status.STOPPED;
        }
    }

    private void printStatus() {
        final Status st = getRemoteStatus();
        System.out.println("Remote status is : " + st);

        if (st == Status.STARTED) {
            try {
                final List<ScanStatus> inprogress = getRemoteInterface().getInprogressScanning();
                if (inprogress.isEmpty()) {
                    System.out.println("No process in progress.");
                } else {
                    System.out.println("In progress process : ");
                    for (ScanStatus s : inprogress) {
                        System.out.println("\t- " + s);
                    }
                }
            } catch (RemoteException e) {
                System.err.println("Can't display inprogress process : " + e.getMessage());
            }
        }

    }

    /**
     * If server isn't started, start it and wait until it's closed.
     *
     * @return TRUE if server is started. FALSE if there is another server started.
     */
    private boolean start() {
        final Status st = getRemoteStatus();

        if (st != Status.STOPPED) {
            return false;
        }

        try {
            LOGGER.info("Starting application...");

            final ContextLauncher launcher =
                    new ContextLauncher(clientConfig.getConfigFile(), clientConfig.getPortGivenInArgument());

            // Start application and detach process from consoles (close input/output streams)
            launcher.start();

            // Wait program end...
            launcher.waitApplicationStarted();
            detachProcess();

            launcher.join();

        } catch (InterruptedException e) {
            LOGGER.warn("Daemon has been interrupted.");
        }

        return true;
    }

    /**
     * Detach process from shell closing input and output stream.
     */
    private static void detachProcess() {
        try {
            System.out.close();
            System.err.close();
            System.in.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing input stream.");
        }
    }

    private MediaManagerRMI getRemoteInterface() {
        // TODO have it aware of real configuration.
        if (remoteInterface == null) {
            try {
                String url = String.format("rmi://localhost:%s/MediaManagerRMI",
                                           clientConfig.getValue("remotecontrol.port"));
                remoteInterface = (MediaManagerRMI) Naming.lookup(url);

            } catch (Exception e) {
                LOGGER.debug("Can't bind server remote interface : {}", e.getMessage(), e);
                remoteInterface = new StoppedRemoteInterface();
            }
        }

        return remoteInterface;
    }

    /** Read port from command line and cast it to int */
    private static int castPort(final String optionValue) {
        try {
            return Integer.parseInt(optionValue);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Port must be an integer, but was : %s", optionValue);
        }
    }
}
