package fr.dush.mediamanager.business.scanner;

import static com.google.common.collect.Sets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.business.mediatech.IRootDirectoryManager;
import fr.dush.mediamanager.business.scanner.impl.AbstractScanner;
import fr.dush.mediamanager.business.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.domain.tree.MediaType;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.events.scan.ScanResponseEvent;
import fr.dush.mediamanager.exceptions.ScanException;

/**
 * Listen {@link RootDirectory}'s events to create and scan directory's contents and find valuable medias.
 *
 * <p>
 * Warning : this bean is accessible only with events, no interface.
 * </p>
 *
 * @author Thomas Duchatelle
 *
 */
public class ScanListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScanListener.class);

	@Inject
	private Event<ScanResponseEvent> scanBus;

	@Inject
	private Instance<MoviesScanner> moviesScannerProvider;

	@Inject
	private IRootDirectoryManager rootDirectoryManager;

	/**
	 * Create new directory and scan it, or refresh existing.
	 *
	 * @param event
	 */
	public void scanDirectory(@Observes ScanRequestEvent event) {
		try {
			final RootDirectory rootDirectory = rootDirectoryManager.createOrUpdateRootDirectory(event.getRootDirectory());

			ScanStatus status = null;
			if (rootDirectory.getLastRefresh() == null) {
				LOGGER.info("Scan new root directory {} ; lastRefresh = {}", rootDirectory, rootDirectory.getLastRefresh());
				// New directory
				status = fullScan(event, rootDirectory);

			} else {
				LOGGER.info("Update root directory {}", rootDirectory);

				// Update directory
				final Set<String> subPaths = newHashSet();
				if (isNotEmpty(event.getSubPath())) {
					subPaths.add(event.getSubPath());
				} else {
					subPaths.addAll(event.getRootDirectory().getPaths());
				}

				status = refresh(event, rootDirectory, subPaths);
			}

			scanBus.fire(new ScanResponseEvent(this, event, status));

		} catch (Exception e) {
			LOGGER.error("Can scan rootdirectory {}.", event.getRootDirectory(), e);
			scanBus.fire(new ScanResponseEvent(this, event, new ScanStatus("Scan didn't start.", e)));
		}

	}

	private ScanStatus fullScan(ScanRequestEvent event, RootDirectory rootDirectory) throws ScanException {

		return getScanner(rootDirectory.getMediaType()).startScanning(rootDirectory);
	}

	private ScanStatus refresh(ScanRequestEvent event, RootDirectory rootDirectory, Set<String> paths) throws ScanException {
		// TODO implement this method : check/update RootDirectory, refresh content.
		LOGGER.warn("refresh root directory is not implemented, use full scan...");
		return fullScan(event, rootDirectory);
	}

	private AbstractScanner<?, ?> getScanner(MediaType type) throws ScanException {
		switch (type) {
			case MOVIE:
				return moviesScannerProvider.get();

			default:
				throw new ScanException("No scanner for media : " + type);
		}
	}
}
