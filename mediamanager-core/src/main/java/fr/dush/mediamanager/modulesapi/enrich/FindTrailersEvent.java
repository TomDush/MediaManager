package fr.dush.mediamanager.modulesapi.enrich;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.media.Media;

/**
 * Event to find all trailers available for an media. This media is directly completed...
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindTrailersEvent implements Serializable {

	private Object source;

	private Media media;

	private String lang = "en";

	public FindTrailersEvent(Object source, Media media) {
		this.source = source;
		this.media = media;
	}

}
