package fr.dush.mediamanager.domain.media;

import java.util.Arrays;

/**
 * {@link fr.dush.mediamanager.domain.tree.RootDirectory}'s media type : key to determine which scanner to use.
 *
 * @author Thomas Duchatelle
 *
 */
public enum MediaType {

	MOVIE,

	SHOWS;

	public static MediaType valueOfMediaType(String value) {
		try {
			return MediaType.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("'%s' is not valid MediaType. Expected : %s", value, Arrays.toString(MediaType.values())));
		}
	}
}
