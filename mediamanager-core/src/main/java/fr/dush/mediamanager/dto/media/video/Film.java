package fr.dush.mediamanager.dto.media.video;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.dto.media.Media;

/**
 * Media is film type.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class Film extends Media {

	/** Video files : can have multiple quality, or version, or cds ... */
	private Set<VideoFile> videoFiles = newHashSet();

	/** Release date */
	private Date release;

	/** 1 - 5 main actors */
	private List<Person> mainActors = newArrayList();

	/** Film director */
	private Person director;

	/** Film types : actions, comedy, ... */
	private Set<String> types = newHashSet();

	/** Downloaded trailers */
	private Set<VideoFile> trailers = newHashSet();
}
