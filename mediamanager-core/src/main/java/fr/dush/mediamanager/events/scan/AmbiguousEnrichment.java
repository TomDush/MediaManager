package fr.dush.mediamanager.events.scan;

import static com.google.common.collect.Lists.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.media.Media;

/**
 * While scanning, if no or more than one media found for one file, this event is fire.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class AmbiguousEnrichment {

	private Object source;

	private Class<? extends Media> mediaClass;

	private Path file;

	/** If empty, no media found for this file, if size > 2, too many found. */
	private List<Media> medias = newArrayList();

	@SuppressWarnings("unchecked")
	public <M extends Media> AmbiguousEnrichment(Object source, Class<M> mediaClass, Path file, List<M> medias) {
		this.source = source;
		this.mediaClass = mediaClass;
		this.file = file;
		this.medias = (List<Media>) Collections.unmodifiableList(medias);
	}

}
