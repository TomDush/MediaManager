package fr.dush.mediamanager;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.dto.tree.MediaType;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.launcher.ContextLauncher;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import fr.dush.mediamanager.remote.impl.StoppedRemoteInterface;

/**
 * Media center main class (containing mathod <code>main</code>
 *
 * @author Thomas Duchatelle
 *
 */
public class MediaManager {

	private static final int DEFAULT_WIDTH = 800;

	private static final Logger LOGGER = LoggerFactory.getLogger(MediaManager.class);

	/** Command line options (parser) */
	private static final Options OPTIONS = buildOptions();

	/** Configuration if none is defined. */
	private static final Path DEFAULT_CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".mediamanager", "mediamanager.properties");

	/** Port's default value, it can be override by command args, or in configuration file. */
	private static final int DEFAULT_PORT = 3535;

	private final CommandLine args;

	private final Path configFile;

	private final int port;

	private MediaManagerRMI remoteInterface;

	public static void main(String[] argsArray) {

		CommandLineParser parser = new GnuParser();

		try {
			final CommandLine args = parser.parse(OPTIONS, argsArray);

			// "Meta" commands
			if (args.hasOption('h')) {
				printHelp(System.out);

			} else if (args.hasOption('v')) {
				printVersion();

			} else {
				final MediaManager mediaManager = new MediaManager(args);
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
		options.addOption("p", "port", true, "port used to communicate with daemon, default is " + DEFAULT_PORT);
		options.addOption(OptionBuilder.withDescription("configuration file, default is " + DEFAULT_CONFIG_PATH).hasArg().withLongOpt("config")
				.withArgName("config file").create("c"));

		// Control daemon
		options.addOption("s", "start", false, "start daemon, if it isn't started");
		options.addOption(OptionBuilder.withDescription("stop daemon, if it started").withLongOpt("stop").create());
		options.addOption(OptionBuilder.withDescription("get daemon status, and scanning progression if any").withLongOpt("status")
				.create());

		options.addOption(OptionBuilder
				.withDescription("scan given directory with defined scanner, server must be started. Can be used with --enricher")
				.hasArgs(2).withArgName("media type> <directoryPath").withLongOpt("scan").create());
		options.addOption(OptionBuilder.withDescription("Used with --scan : define default enricher.").hasArgs(1).withArgName("enricher")
				.withLongOpt("enricher").create());
		options.addOption(OptionBuilder.withDescription("show current configuration").withLongOpt("show").create());
		options.addOption(OptionBuilder.withDescription("set variable").hasArgs(3).withArgName("package> <name> <value").withLongOpt("set")
				.create());

		return options;
	}

	/**
	 * Launcher constructor to interpret and execute args.
	 *
	 * @param args
	 */
	public MediaManager(CommandLine args) {
		this.args = args;

		if (args.hasOption('c')) {
			configFile = Paths.get(args.getOptionValue('c'));
			if (!configFile.toFile().exists()) {
				throw new ConfigurationException("Configuration file %s doesn't exist.", configFile);
			}
		} else if (DEFAULT_CONFIG_PATH.toFile().exists()) {
			configFile = DEFAULT_CONFIG_PATH;
		} else {
			configFile = null;
		}

		if (args.hasOption('p')) {
			port = castPort(args.getOptionValue('p'));
		} else if (configFile != null) {
			port = readPort(configFile);
		} else {
			port = DEFAULT_PORT; // FIXME What is this port ??
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
		if (args.hasOption("start") || args.getOptions().length == 0) {
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
				getRemoteInterface().scan(MediaType.valueOfMediaType(arguments[0]), toAbsolute(arguments[1]),
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
					System.out.println("No copnfiguration loaded...");
				}

				return;
			}

			System.out.println("Configuration : ");
			for (ConfigurationField f : configs) {
				System.out
						.println(String.format("\t- %-40s = %s %s", f.getFullname(), f.getValue(), f.isDefaultValue() ? "(default)" : ""));
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
	 * If server isn't start, this process will be the server.
	 *
	 * @return TRUE if server is started. FALSE if there is another server started.
	 */
	private boolean start() {
		final Status st = getRemoteStatus();

		if (st != Status.STOPPED) {
			return false;
		}

		try {
			final ContextLauncher launcher = new ContextLauncher(configFile, port);
			synchronized (launcher) {
				launcher.start();

				launcher.wait();
			}

			detachProcess();

			// Wait program end...
			launcher.join();

		} catch (InterruptedException e) {
			LOGGER.warn("Daemon has been interrupted.");
		}

		return true;
	}

	/**
	 * Detach process from shell closing input and output stream.
	 */
	private void detachProcess() {
		try {
			System.out.close();
			System.err.close();
			System.in.close();
		} catch (IOException e) {
			LOGGER.error("Error while closing input stream.");
		}
	}

	private MediaManagerRMI getRemoteInterface() {
		if (remoteInterface == null) {
			try {
				remoteInterface = (MediaManagerRMI) Naming.lookup("rmi://localhost/" + MediaManagerRMI.class.getSimpleName());

			} catch (Exception e) {
				LOGGER.debug("Can't bind server remote interface : {}", e.getMessage(), e);
				remoteInterface = new StoppedRemoteInterface();
			}
		}

		return remoteInterface;
	}

	/**
	 * Read defined port (in config file), or return default.
	 *
	 * @param configFile
	 * @return
	 */
	private static int readPort(Path configFile) {
		final File file = configFile.toFile();
		if (!file.exists()) {
			return DEFAULT_PORT;
		}

		try {
			Properties props = new Properties();
			props.load(new FileInputStream(file));

			return castPort(props.getProperty("mediamanager.port", String.valueOf(DEFAULT_PORT)));

		} catch (IOException e) {
			LOGGER.warn("Can't read configuration file {} : {}", configFile, e.getMessage());
			return DEFAULT_PORT;
		}
	}

	private static int castPort(final String optionValue) {
		try {
			return Integer.parseInt(optionValue);
		} catch (NumberFormatException e) {
			throw new ConfigurationException("Port must be an integer, but was : %s", optionValue);
		}
	}
}
