package fr.dush.mediamanager.config;

import fr.dush.mediamanager.business.configuration.SimpleConfiguration;
import lombok.Getter;

import java.nio.file.Path;

/**
 * Utility class to create static configuration : the one who can be override by database.
 */
public class ClientConfiguration extends SimpleConfiguration {

    @Getter
    private final Path configFile;

    /** Port as given, may be null. */
    private final Integer port;

    public ClientConfiguration(Path configFile, Integer port) {
        super(configFile, port, "remotecontrol");
        this.configFile = configFile;
        this.port = port;
    }

    /** This port may be null because it has been resolved. */
    public Integer getPortGivenInArgument() {
        return port;
    }

    /** All keys must be absolute */
    public String getValue(String key) {
        // Use System.getProperty as failover but fieldSets default values has already been override by system!
        return getProperties().getProperty(key, System.getProperty(key));
    }
}
