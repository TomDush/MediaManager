package fr.dush.mediamanager.domain.media.video;

import fr.dush.mediamanager.domain.media.MediaFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static com.google.common.collect.Lists.*;

/**
 * Video file.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
@NoArgsConstructor
public class VideoFile extends MediaFile {

	/** Video quality */
	private Quality quality;

    // TODO Fill quality and add version

	/** Next files if video is split between multiple part. */
	private List<String> nextParts = newArrayList();

	public VideoFile(Path file) {
		super(file);
	}

	/**
	 * If file is cut between multiple files. If true, this instance
	 *
	 * @return
	 */
	public boolean isMultipart() {
		return !nextParts.isEmpty();
	}

	@Override
	public String toString() {
		return "VideoFile [getFile()=" + getFile() + ", nextParts=" + nextParts + "]";
	}
}
