package fr.dush.mediamanager.domain.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Configuration field
 *
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor
public class Field {

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

    /** Create field with NON-default value */
    public Field(String key, String value) {
        this(key, value, false);
    }

    public Field(String key, String value, boolean defaultValue) {
        this.key = key;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    /**
     * Update this object with other field. Override key and value. Other attribute are updated is there are not empty.
     */
    public void merge(Field field) {
        key = field.key;
        value = field.value;
        defaultValue = field.defaultValue;

        updateDisplayable(field.name, field.description);
    }

    public void updateDisplayable(String newName, String newDescription) {
        if (isNotEmpty(newName)) {
            name = newName;
        }
        if (isNotEmpty(newDescription)) {
            description = newDescription;
        }
    }

    public void setValue(String value) {
        this.value = value;
        this.defaultValue = false;
    }
}
