package fr.dush.mediamanager.engine.festassert.configuration;

import org.fest.assertions.api.Assertions;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dto.configuration.Field;
import fr.dush.mediamanager.dto.media.video.Movie;
import fr.dush.mediamanager.dto.media.video.MovieAssert;

public class MediaManagerAssertions extends Assertions {

	public static FieldAssert assertThat(Field actual) {
		return new FieldAssert(actual);
	}

	public static ModuleConfigurationAssert assertThat(ModuleConfiguration actual) {
		return new ModuleConfigurationAssert(actual);
	}

	public static MovieAssert assertThat(Movie actual) {
		return new MovieAssert(actual);
	}
}