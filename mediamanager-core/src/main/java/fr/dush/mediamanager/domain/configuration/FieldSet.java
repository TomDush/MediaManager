package fr.dush.mediamanager.domain.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Simple data object which group properties of each module.
 *
 * @author Thomas Duchatelle
 */
@Data
@EqualsAndHashCode(of = "configId")
@NoArgsConstructor
public class FieldSet {

    /**
     * Module configuration identifier
     */
    private String configId;

    /**
     * Module display name
     */
    private String name;

    /**
     * Configuration's values key => value
     */
    private Map<String, Field> fields = newHashMap();

    public FieldSet(String configId) {
        this.configId = configId;
    }

    /**
     * Add or Set value
     *
     * @param defaultValue if this value is default value...
     */
    public void addValue(String key, String value, boolean defaultValue) {
        if (fields.containsKey(key)) {
            final Field f = fields.get(key);
            f.setValue(value);
            f.setDefaultValue(defaultValue);

        } else {
            fields.put(key, new Field(key, value));
        }
    }

    public void addAllFields(List<Field> fields, boolean b) {
        for (Field f : fields) {
            addField(f);
            f.setDefaultValue(b);
        }
    }

    /**
     * Add field, replace existing one if any.
     */
    private void addField(Field f) {
        fields.put(f.getKey(), f);
    }

    @Override
    public String toString() {
        return "FieldSet [configId=" + configId + ", name=" + name + ", fields=" + fields + "]";
    }

}
