package fr.dush.mediamanager.domain.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;

/**
 * Simple data object which group properties of each module.
 *
 * @author Thomas Duchatelle
 */
@ToString
@EqualsAndHashCode(of = "configId")
@NoArgsConstructor
public class FieldSet {

    /** Module configuration identifier */
    @Getter
    @Setter
    private String configId;

    /** Module display name */
    @Getter
    @Setter
    private String name;

    /** Configuration's values key => value */
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

    public void addAllFields(Collection<Field> fields, boolean b) {
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

    @JsonIgnore
    public Map<String, Field> getFieldMap() {
        return fields;
    }

    public List<Field> getFields() {
        return new ArrayList<>(fields.values());
    }

    public void setFields(List<Field> fields) {
        addAllFields(fields, true);
    }

    /** Load given map with values from this field set. */
    public void loadMap(Map<Object, Object> props) {
        for (Field field : getFields()) {
            // Fields are set with absolute path
            props.put(getConfigId() + "." + field.getKey(), field.getValue());
        }
    }
}
