package fr.dush.mediamanager.engine.mock;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.domain.configuration.Field;
import fr.dush.mediamanager.domain.configuration.FieldSet;

public class MockedConfiguration extends ModuleConfiguration {

    /**
     * Initialize ModuleConfiguration
     *
     * @param keyValue Impair arguments are keys, pairs are values.
     */
    public MockedConfiguration(String... keyValue) {
        super("foo", new FieldSet());
        if (keyValue.length % 2 != 0) {
            throw new IllegalArgumentException("keyValue must have pair elements.");
        }

        for (int i = 0; i < keyValue.length; i += 2) {
            addField(new Field(keyValue[i], keyValue[i + 1]));
        }
    }

}
