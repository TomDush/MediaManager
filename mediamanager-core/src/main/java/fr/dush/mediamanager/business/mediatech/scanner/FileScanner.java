package fr.dush.mediamanager.business.mediatech.scanner;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.InprogressScanning;
import fr.dush.mediamanager.events.scan.NewRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.RefreshRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.ScanningErrorEvent;
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
public class FileScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileScanner.class);

	@Inject
	private Event<InprogressScanning> inprogressBus;

	@Inject
	private Event<ScanningErrorEvent> scanningErrorEvent;

	@Inject
	private Instance<MoviesScanner> moviesScanner;

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	/**
	 * Save {@link RootDirectory} and begin to scan it. Fire {@link InprogressScanning} if scanning started ; else fire
	 * {@link ScanningErrorEvent}...
	 *
	 * @param event
	 */
	public void scanNewDirectory(@Observes NewRootDirectoryEvent event) {

		LOGGER.debug("--> scanNewDirectory {}", event);

		try {
			// Try to save root directory
			rootDirectoryDAO.persist(event.getRootDirectory());

			// Get enricher
			final ScanningStatus status = moviesScanner.get().startScanning(event.getRootDirectory());

			// Fire event
			inprogressBus.fire(new InprogressScanning(this, event.getRootDirectory(), status));

		} catch (RootDirectoryAlreadyExistsException | ScanningException e) {
			scanningErrorEvent.fire(new ScanningErrorEvent(this, event.getRootDirectory(), e));
		}

	}

	public void refreshRootDirectory(@Observes RefreshRootDirectoryEvent event) {
		LOGGER.warn("refreshRootDirectory is not implemented. Can't refresh directory : {}.", event);

		scanningErrorEvent.fire(new ScanningErrorEvent(this, event.getRootDirectory(), new RuntimeException(
				"refreshRootDirectory is not implemented.")));

		// TODO implement this method : check/update RootDirectory, refresh content.
	}
}
