package fr.dush.mediamanager.business.mediatech.scanner;

import static com.google.common.collect.Lists.*;

import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import fr.dush.mediamanager.business.mediatech.scanner.impl.MoviesScanner;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.reponses.InprogressScanningResponseEvent;
import fr.dush.mediamanager.events.scan.reponses.ScanningErrorResponseEvent;
import fr.dush.mediamanager.events.scan.request.AbstractRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.request.NewRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.request.RefreshRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.request.ScanningRequestEvent;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;
import fr.dush.mediamanager.exceptions.ScanningException;

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
	private Event<InprogressScanningResponseEvent> inprogressBus;

	@Inject
	private Event<ScanningErrorResponseEvent> scanningErrorEvent;

	@Inject
	private Instance<MoviesScanner> moviesScanner;

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	/**
	 * Save {@link RootDirectory} and begin to scan it. Fire {@link InprogressScanningResponseEvent} if scanning started ; else fire
	 * {@link ScanningErrorResponseEvent}...
	 *
	 * @param event
	 */
	public void scanNewDirectory(@Observes NewRootDirectoryEvent event) {

		LOGGER.debug("--> scanNewDirectory {}", event);
		Object source = getSourceEvent(event);

		try {
			// Try to save root directory
			rootDirectoryDAO.persist(event.getRootDirectory());

			// Get enricher
			final ScanningStatus status = moviesScanner.get().startScanning(event.getRootDirectory());

			// Fire event
			inprogressBus.fire(new InprogressScanningResponseEvent(this, source, event.getRootDirectory(), status));

		} catch (RootDirectoryAlreadyExistsException | ScanningException e) {
			scanningErrorEvent.fire(new ScanningErrorResponseEvent(this, source, event.getRootDirectory(), e));
		}

	}

	public void refreshRootDirectory(@Observes RefreshRootDirectoryEvent event) {
		LOGGER.warn("refreshRootDirectory is not implemented. Can't refresh directory : {}.", event);

		scanningErrorEvent.fire(new ScanningErrorResponseEvent(this, getSourceEvent(event), event.getRootDirectory(),
				"refreshRootDirectory is not implemented."));

		// TODO implement this method : check/update RootDirectory, refresh content.
	}

	/**
	 * Create new directory and scan it, or refresh existing.
	 *
	 * @param event
	 */
	public void scanDirectory(@Observes ScanningRequestEvent event) {
		final List<RootDirectory> roots = rootDirectoryDAO.findUsingPath(newArrayList(event.getPath()));

		if (roots.isEmpty()) {
			// Create new rootdirectory, and scan it
			RootDirectory rootDirectory = new RootDirectory(Files.getNameWithoutExtension(event.getPath()), event.getScannerName(),
					event.getPath());
			scanNewDirectory(new NewRootDirectoryEvent(event, rootDirectory));

		} else if (roots.size() == 1) {
			refreshRootDirectory(new RefreshRootDirectoryEvent(event, roots.get(0)));

		} else {
			final String error = String.format("%s correspond to multiple RootDirectory. You must clean your database...", event.getPath());
			scanningErrorEvent.fire(new ScanningErrorResponseEvent(this, event, null, error));
		}
	}

	private Object getSourceEvent(AbstractRootDirectoryEvent event) {
		Object source = event;
		if (event.getSource() instanceof ScanningRequestEvent) {
			source = event.getSource();
		}
		return source;
	}
}
