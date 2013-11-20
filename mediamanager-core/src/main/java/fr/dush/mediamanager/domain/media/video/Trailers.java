package fr.dush.mediamanager.domain.media.video;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * Trailers list.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
public class Trailers implements Serializable {

	/** Last date when this list has been refreshed */
	private Date refreshed;

	/** Trailer sources used to build list */
	private Set<String> sources = newHashSet();

	/** Trailer list */
	private List<Trailer> trailers = newArrayList();

	/**
	 * Add trailer avoiding double.
	 * @param trailer
	 */
	public void addTrailer(Trailer trailer) {
		// Trailer's hashcode and equals are on #url to avoid double.
		if (!trailers.contains(trailer)) {
			trailers.add(trailer);
		}

	}

}
