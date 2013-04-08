package fr.dush.mediamanager.dto.media;

import java.io.Serializable;

import lombok.Data;

/**
 * Media unique identifier. Each media can have multiple identifiers from multiples sources.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
public class MediaId implements Serializable {

	/** Identifier source. Exemple : internal, imdb, ... */
	private String type;

	/** Idenfier value */
	private String value;

}
