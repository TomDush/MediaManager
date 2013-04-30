package fr.dush.mediamanager.dto.media;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Media unique identifier. Each media can have multiple identifiers from multiples sources.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
public class SourceId implements Serializable {

	/** Identifier source. Exemple : internal, imdb, ... */
	private String type;

	/** Idenfier value */
	private String value;

	public SourceId(String type, String value) {
		this.type = type;
		this.value = value;
	}



}
