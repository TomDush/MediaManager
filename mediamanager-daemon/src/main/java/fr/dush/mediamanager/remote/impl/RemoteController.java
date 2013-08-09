package fr.dush.mediamanager.remote.impl;

import static com.google.common.collect.Lists.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

import fr.dush.mediamanager.business.configuration.IConfigurationRegister;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.events.scan.reponses.InprogressScanningResponseEvent;
import fr.dush.mediamanager.events.scan.reponses.ScanningErrorResponseEvent;
import fr.dush.mediamanager.events.scan.reponses.ScanningResponseEvent;
import fr.dush.mediamanager.events.scan.request.AbstractRootDirectoryEvent;
import fr.dush.mediamanager.events.scan.request.ScanningRequestEvent;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import fr.dush.mediamanager.remote.Stopper;

/**
 * Bean exposed by RMI : execute on server what is asked from command line.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
//@ApplicationScoped
public class RemoteController extends UnicastRemoteObject implements MediaManagerRMI {

	private static final int TIMEOUT = 3000;

	@Inject
	private Stopper stopper;

	@Inject
	private IRootDirectoryDAO rootDirectoryDAO;

	@Inject
	private Event<ScanningRequestEvent> scanningRequestEventBus;

	@Inject
	private IConfigurationRegister configurationRegister;

	@Inject
	private IMovieDAO movieDAO;

	private static ResponseReceiver responseReceiver;

	public RemoteController() throws RemoteException {
		super();
	}

	@PostConstruct
	public void initialize() {
		if(responseReceiver == null) {
			responseReceiver = new ResponseReceiver();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteController.class);

	@Override
	public synchronized void stop() {
		LOGGER.info("Stopping application on {}", stopper);

		stopper.stopApplication();
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}

	@Override
	public void scan(String scannerName, String absolutePath) throws RemoteException {
		try {
			final ScanningRequestEvent event = new ScanningRequestEvent(this, absolutePath, scannerName);
			scanningRequestEventBus.fire(event);

			final ScanningResponseEvent response = responseReceiver.waitForResponse(event);
			if (response == null) {
				throw new RemoteException("No response received... Ask status command to know if process has been start.");

			} else if (response instanceof ScanningErrorResponseEvent) {
				throw new RemoteException("Error, can't scan of " + absolutePath + " : "
						+ ((ScanningErrorResponseEvent) response).getMessage(), ((ScanningErrorResponseEvent) response).getException());
			}
		} catch (RemoteException e) {
			LOGGER.error("{}", e.getMessage(), e);
			throw e;

		} catch (Exception e) {
			LOGGER.error("Start scanning failed", e);
			throw new RemoteException("Can't start scan : " + e.getMessage());
		}
	}

	@Override
	public List<ScanningStatus> getInprogressScanning() throws RemoteException {
		try {
			final List<ScanningStatus> list = responseReceiver.getInprogressStatus();
			return newArrayList(list);
		} catch (Exception e) {
			LOGGER.error("getInprogressScanning failed", e);
			throw new RemoteException("Can't get Inprogress list : " + e.getMessage());
		}
	}

	@Override
	public List<ConfigurationField> getFullConfiguration() throws RemoteException {
		try {
			movieDAO.findAll(); // FIXME to remove : force init...

			List<ConfigurationField> list = newArrayList();

			for (ModuleConfiguration m : configurationRegister.findAll()) {
				for (Field f : m.getAllFields()) {
					ConfigurationField field = new ConfigurationField();
					field.setFullname(m.getPackageName() + "." + f.getKey());
					field.setValue(m.readValue(f.getKey()));
					field.setDefaultValue(f.isDefaultValue());
					field.setDescription(f.getDescription());

					list.add(field);
				}
			}

			Collections.sort(list);
			return list;
		} catch (Exception e) {
			LOGGER.error("getFullConfiguration failed", e);
			throw new RemoteException("Can't get configuration : " + e.getMessage());
		}
	}

	public void observeResponse(@Observes ScanningResponseEvent event) {
		responseReceiver.add(event);
	}

	private class ResponseReceiver {

		private List<ScanningResponseEvent> events = newArrayList();

		public synchronized void add(ScanningResponseEvent event) {
			// Clean old inprogress process...
			cleanOldEvents();

			// Add new one
			events.add(event);

			notifyAll();
		}

		public List<ScanningStatus> getInprogressStatus() {
			cleanOldEvents();

			return transform(events, new Function<AbstractRootDirectoryEvent, ScanningStatus>() {
				@Override
				public ScanningStatus apply(AbstractRootDirectoryEvent e) {
					if (e instanceof InprogressScanningResponseEvent) {
						return ((InprogressScanningResponseEvent) e).getStatus();
					} else if (e instanceof ScanningErrorResponseEvent) {
						return new ScanningStatus(e.getRootDirectory().getName() + " process failed. "
								+ ((ScanningErrorResponseEvent) e).getMessage());
					}

					return null;
				}
			});
		}

		private void cleanOldEvents() {
//			for (AbstractRootDirectoryEvent e : newArrayList(events)) {
//				if (e instanceof InprogressScanningResponseEvent && !((InprogressScanningResponseEvent) e).getStatus().isInProgress()) {
//					events.remove(e);
//				}
//			}
		}

		public synchronized ScanningResponseEvent waitForResponse(Object sourceEvent) {
			ScanningResponseEvent response = hasResponse(sourceEvent);

			if (response == null) {
				try {
					wait(TIMEOUT);
				} catch (InterruptedException e) {
				}
				response = hasResponse(sourceEvent);
			}

			return response;
		}

		private ScanningResponseEvent hasResponse(Object sourceEvent) {
			for (ScanningResponseEvent e : events) {
				if (sourceEvent.equals(e.getEventSource())) {
					return e;
				}
			}

			return null;
		}

	}
}
