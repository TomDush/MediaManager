package fr.dush.mediamanager.engine.festassert.configuration;

import org.fest.assertions.api.Assertions;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dto.configuration.Field;

public class MediaManagerAssertions extends Assertions {

	/**
	 * An entry point for FieldAssert to follow Fest standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myField)</code> and get specific assertion with code completion.
	 *
	 * @param actual the Field we want to make assertions on.
	 * @return a new </code>{@link FieldAssert}</code>
	 */
	public static FieldAssert assertThat(Field actual) {
		return new FieldAssert(actual);
	}

	/**
	 * An entry point for ModuleConfigurationAssert to follow Fest standard <code>assertThat()</code> statements.<br>
	 * With a static import, one's can write directly : <code>assertThat(myModuleConfiguration)</code> and get specific assertion with code
	 * completion.
	 *
	 * @param actual the ModuleConfiguration we want to make assertions on.
	 * @return a new </code>{@link ModuleConfigurationAssert}</code>
	 */
	public static ModuleConfigurationAssert assertThat(ModuleConfiguration actual) {
		return new ModuleConfigurationAssert(actual);
	}
}
