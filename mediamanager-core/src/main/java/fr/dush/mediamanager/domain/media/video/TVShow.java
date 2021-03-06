package fr.dush.mediamanager.domain.media.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.domain.media.Media;

/**
 * Represent TV Show (multi seasons).
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class TVShow extends Media {

	/** Number of existing seasons */
	private int seasonsNumber;

}
