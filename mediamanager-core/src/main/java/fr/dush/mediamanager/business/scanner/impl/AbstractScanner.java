package fr.dush.mediamanager.business.scanner.impl;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.mediatech.IRootDirectoryManager;
import fr.dush.mediamanager.dto.configuration.ScannerConfiguration;
import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.scan.Phase;
import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.ScanException;

/**
 * DO NOT USE AS SINGLETON.
 *
 * @author Thomas Duchatelle
 *
 */
public abstract class AbstractScanner<F, M extends Media> implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScanner.class);

	/** Scanner's configuration */
	@Inject
	@Getter
	private ScannerConfiguration scannerConfiguration;

	@Inject
	private Instance<ScanningExceptionHandler> scanningExceptionHandlerFactory;

	@Inject
	private IRootDirectoryManager rootDirectoryManager;

	/** Progress status */
	private ScanStatus status;

	private RootDirectory rootDirectory;

	/** Root paths to scan */
	private Set<Path> rootPaths = newHashSet();

	public ScanStatus startScanning(RootDirectory rootDirectory) throws ScanException {
		this.rootDirectory = rootDirectory;

		// Scanning recursively directory. Put parsed file's name into ScanningResult
		for (String path : rootDirectory.getPaths()) {
			final File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				rootPaths.add(dir.toPath());

			} else {
				StringBuffer sb = new StringBuffer(dir.getPath());
				sb.append(" can't be scanning because ");
				if (!dir.exists()) {
					sb.append(" it doesn't exist.");
				} else {
					sb.append(" it not a directory.");
				}

				throw new ScanException(sb.toString());
			}
		}

		// Initialize status, and thread.
		final Thread thread = new Thread(this);
		status = new ScanStatus();

		// Starting job(s) in other thread
		thread.setUncaughtExceptionHandler(newScanningExceptionHandler(status));
		thread.start();

		return status;
	}

	private UncaughtExceptionHandler newScanningExceptionHandler(ScanStatus handlerStatus) {
		ScanningExceptionHandler handler = scanningExceptionHandlerFactory.get();
		handler.setStatus(handlerStatus);

		return handler;
	}

	/**
	 * Start scanning files...
	 */
	@Override
	public void run() {
		final List<F> files = newArrayList();

		// ** PHASE 1 : scan files
		LOGGER.info("Start scanning roots : {}", rootPaths);
		status.changePhase(Phase.SCANNING, rootPaths.size() > 1 ? rootPaths.size() : 0);

		for (Path root : rootPaths) {
			LOGGER.debug("--> Scanning {}", root);
			status.setStepName("Scanning " + root);

			files.addAll(scanDirectory(root));
			status.incrementFinishedJob(1);
		}

		// ** PHASE 2 : enrich and save
		LOGGER.info("Start enrichment of {} medias.", files.size());
		status.changePhase(Phase.ENRICH, files.size(), "Enrichment...");

		for (F file : files) {
			final M media = enrich(file);
			if (media != null) {
				save(media);
			} else {
				LOGGER.warn("No media created for {}", file);
			}

			status.incrementFinishedJob(1);
		}

		// ** FINISH
		LOGGER.info("Finish enrichment of {}", rootPaths);
		status.changePhase(Phase.SUCCED, 0);
		rootDirectoryManager.markAsUpdated(rootDirectory);
	}

	/** Save media */
	protected abstract void save(M media);

	/**
	 * Get directory file list. TODO Filter file to ignore.
	 *
	 * @param root
	 * @return
	 */
	protected File[] getChildren(File root) {
		return root.listFiles();
	}

	protected abstract Collection<? extends F> scanDirectory(Path root);

	/**
	 * Create media file
	 *
	 * @param file
	 * @return Must not be null
	 */
	protected abstract M enrich(F file);

}
