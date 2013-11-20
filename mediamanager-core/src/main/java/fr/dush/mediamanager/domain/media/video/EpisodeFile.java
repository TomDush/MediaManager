package fr.dush.mediamanager.domain.media.video;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Episode name : add to {@link VideoFile} episode informations.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class EpisodeFile extends VideoFile {

	/** Episode number */
	private int number;

	/** Episode name, if available*/
	private String name;
}
