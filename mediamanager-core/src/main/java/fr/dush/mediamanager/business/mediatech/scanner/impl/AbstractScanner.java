package fr.dush.mediamanager.business.mediatech.scanner.impl;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus.Phase;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.configuration.ScannerConfiguration;
import fr.dush.mediamanager.dto.media.Media;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.reponses.AmbiguousEnrichment;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;
import fr.dush.mediamanager.exceptions.ScanningException;

/**
 * DO NOT USE AS SINGLETON.
 *
 * @author Thomas Duchatelle
 *
 */
public abstract class AbstractScanner<F> implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScanner.class);

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	/** Scanner's configuration */
	@Inject
	protected ScannerConfiguration scannerConfiguration;

	@Inject
	protected Event<AmbiguousEnrichment> ambiguousEnrichmentDispatcher;

	/** Pattern to find date in filenames */
	protected Pattern datePattern;

	/** Pattern matching movies split into 2 or more files. */
	protected Set<Pattern> moviesStackingPatterns = newHashSet();

	/** Progress status */
	private ScanningStatus status;

	/** Root paths to scan */
	private Set<Path> rootPaths = newHashSet();

	@PostConstruct
	public void initializePatterns() {

		// Pre-compile patterns...
		if (isNotEmpty(scannerConfiguration.getDateRegex())) {
			datePattern = Pattern.compile(scannerConfiguration.getDateRegex());
		}

		for (String regex : scannerConfiguration.getMoviesStacking()) {
			moviesStackingPatterns.add(Pattern.compile(regex));
		}
	}

	public ScanningStatus startScanning(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException,
			ScanningException {

		// Scanning recursively directory. Put parsed file's name into ScanningResult
		for (String path : rootDirectory.getPaths()) {
			final File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				rootPaths.add(dir.toPath());

			} else {
				StringBuffer sb = new StringBuffer(dir.getPath());
				sb.append(" can't be scanning because ");
				if (!dir.exists()) sb.append(" it doesn't exist.");
				else sb.append(" it not a directory.");

				throw new ScanningException(sb.toString());
			}
		}

		// Initialize status, and thread.
		final Thread thread = new Thread(this);
		status = new ScanningStatus(thread);

		// Starting job(s) in other thread
		thread.start();
		return status;
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
			final Media media = enrich(file);
			if(media != null) {
				save(media); // FIXME : may not be null !
			}

			status.incrementFinishedJob(1);
		}
	}

	/** Save media */
	protected abstract void save(Media media) ;

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

	protected abstract Media enrich(F file);

}
