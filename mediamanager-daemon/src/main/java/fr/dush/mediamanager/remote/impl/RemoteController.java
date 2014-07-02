package fr.dush.mediamanager.remote.impl;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.IConfigurationManager;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.scanner.IScanRegister;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.IStopper;
import fr.dush.mediamanager.remote.MediaManagerRMI;
import lombok.Setter;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Bean exposed by RMI : execute on server what is asked from command line.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Controller
public class RemoteController extends UnicastRemoteObject implements MediaManagerRMI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteController.class);

    private static final String REMOTE_CONTROL_URL = "url";

    @Config(id = "remotecontrol")
    @Setter
    private ModuleConfiguration configuration;

    @Inject
    private IStopper stopper;

    @Inject
    private EventBus eventBus;

    @Inject
    private IConfigurationManager configurationManager;
    @Inject
    private IScanRegister scanRegister;

    @Inject
    private Mapper dozerMapper;

    public RemoteController() throws RemoteException {
        super();
    }

    /** Register remote interface RMI */
    @PostConstruct
    public void registerRmiImplementation() {
        try {
            startRegistry();

            String url = configuration.readValue(REMOTE_CONTROL_URL);
            LOGGER.debug("Bind RMI controller to url : {}", url);
            Naming.rebind(url, this);
        } catch (Exception e) {
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

            } else if (response.hasFailed()) {
                LOGGER.error("Failed to scan %s", response.getException());
                throw new RemoteException(response.getMessage() + " [on " + absolutePath + "]");
            }

        } catch (RemoteException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Start scanning failed", e);
            throw new RemoteException("Can't start scan : " + e.getMessage());
        }
    }

    @Override
    public List<ScanStatus> getInprogressScanning() throws RemoteException {
        try {
            return scanRegister.getInprogressScans();

        } catch (Exception e) {
            LOGGER.error("getInprogressScanning failed", e);
            throw new RemoteException("Can't get Inprogress list : " + e.getMessage(), e);
        }
    }

    @Override
    public List<ConfigurationField> getFullConfiguration() throws RemoteException {
        try {
            List<ConfigurationField> list = newArrayList();

            for (FieldSet fieldSet : configurationManager.getAllConfigurations()) {
                for (Field f : fieldSet.getFields()) {
                    ConfigurationField field = dozerMapper.map(f, ConfigurationField.class);
                    field.setKey(fieldSet.getConfigId() + "." + f.getKey());

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

    /**
     * Create dynamic registry, if necessary...
     */
    private Registry startRegistry() throws RemoteException {
        final Integer port = configuration.readValueAsInt("port");

        if (configuration.readValueAsBoolean("createregistry")) {
            return LocateRegistry.createRegistry(port);

        } else {
            return LocateRegistry.getRegistry(port);
        }

    }

}
