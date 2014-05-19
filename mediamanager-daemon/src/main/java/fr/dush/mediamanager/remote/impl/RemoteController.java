package fr.dush.mediamanager.remote.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.ConfigurationWithoutDatabase;
import fr.dush.mediamanager.business.configuration.IConfigurationRegister;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.scanner.IScanRegister;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import fr.dush.mediamanager.remote.Stopper;

/**
 * Bean exposed by RMI : execute on server what is asked from command line.
 * 
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Named
public class RemoteController extends UnicastRemoteObject implements MediaManagerRMI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteController.class);

    private static final String REMOTECONTROL_URL = "remotecontrol.url";

    @Inject
    @Configuration(packageName = "daemon", definition = "configuration/rmi.json")
//    @ConfigurationWithoutDatabase
    private ModuleConfiguration configuration;

    @Inject
    private Stopper stopper;

    @Inject
    private EventBus eventBus;

    @Inject
    private IConfigurationRegister configurationRegister;

    @Inject
    private IScanRegister scanRegister;

    public RemoteController() throws RemoteException {
        super();
    }

    /** Register remote interface RMI */
    @PostConstruct
    public void registerRmiImplementation() {
        try {
            startRegistry();

            LOGGER.debug("Bind RMI controller to url : {}", configuration.readValue(REMOTECONTROL_URL));
            Naming.rebind(configuration.readValue(REMOTECONTROL_URL), this);
        }
        catch (Exception e) {
            throw new ConfigurationException("Can't register RMI implementation.", e);
        }
    }

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
    public void scan(MediaType type, String absolutePath, String enricher) throws RemoteException {
        try {
            final ScanRequestEvent event = new ScanRequestEvent(this, type, absolutePath);
            if (isNotEmpty(enricher)) {
                event.getRootDirectory().setEnricher(enricher);
            }

            eventBus.post(event);

            final ScanStatus response = scanRegister.waitResponseFor(event);
            if (response == null) {
                LOGGER.warn("Scan status was not received...");
                throw new RemoteException(
                        "No response received... Execute 'status' command to know if process has been started.");

            }
            else if (response.hasFailed()) {
                LOGGER.error("Failed to scan %s", response.getException());
                throw new RemoteException(response.getMessage() + " [on " + absolutePath + "]");
            }

        }
        catch (RemoteException e) {
            throw e;

        }
        catch (Exception e) {
            LOGGER.error("Start scanning failed", e);
            throw new RemoteException("Can't start scan : " + e.getMessage());
        }
    }

    @Override
    public List<ScanStatus> getInprogressScanning() throws RemoteException {
        try {
            final List<ScanStatus> list = scanRegister.getInprogressScans();
            return newArrayList(list);

        }
        catch (Exception e) {
            LOGGER.error("getInprogressScanning failed", e);
            throw new RemoteException("Can't get Inprogress list : " + e.getMessage());
        }
    }

    @Override
    public List<ConfigurationField> getFullConfiguration() throws RemoteException {
        try {
            List<ConfigurationField> list = newArrayList();

            for (ModuleConfiguration m : configurationRegister.findAll()) {
                for (Field f : m.getAllFields()) {
                    ConfigurationField field = new ConfigurationField();
                    field.setFullname(m.getPackageName() + "." + f.getKey());
                    field.setValue(m.getValue(f.getKey()));
                    field.setDefaultValue(f.isDefaultValue());
                    field.setDescription(f.getDescription());

                    list.add(field);
                }
            }

            Collections.sort(list);
            return list;
        }
        catch (Exception e) {
            LOGGER.error("getFullConfiguration failed", e);
            throw new RemoteException("Can't get configuration : " + e.getMessage());
        }
    }

    /**
     * Create dynamic registry, if necessary...
     * 
     * @throws RemoteException
     */
    private Registry startRegistry() throws RemoteException {
        final Integer port = configuration.readValueAsInt("remotecontrol.port");

        if (configuration.readValueAsBoolean("remotecontrol.createregistry")) {
            return LocateRegistry.createRegistry(port);

        }
        else {
            return LocateRegistry.getRegistry(port);
        }

    }

}
