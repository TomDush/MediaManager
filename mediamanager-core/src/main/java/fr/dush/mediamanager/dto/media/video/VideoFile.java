package fr.dush.mediamanager.dto.media.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.dto.media.MediaFile;

/**
 * Video file.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class VideoFile extends MediaFile {

	/** Video quality */
	private Quality quality;

	/** If film is split on multiple files, part of film. If 0, no multi-part. */
	private int part = 0;
}
