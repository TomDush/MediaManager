package fr.dush.mediamanager.remote;

import lombok.Data;

import java.io.Serializable;

/** Field DTO like. Maybe REST service will need it to. */
@SuppressWarnings("serial")
@Data
public class ConfigurationField implements Serializable, Comparable<ConfigurationField> {

    /** Absolute key: fieldSet id + field id */
    private String key;

    private String value;

    private String name;

    private String description;

    /** Value defined in Json property file */
    private boolean defaultValue = true;

    /**
     * When value come from application properties, or is read by placeholder (application first initialisation), it
     * can't be override
     */
    private boolean staticField = false;

    @Override
    public int compareTo(ConfigurationField arg0) {
        return key.compareTo(this.getKey());
    }
}
