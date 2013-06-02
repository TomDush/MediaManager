package fr.dush.mediamanager.business.mediatech.scanner;

import java.nio.file.Path;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import fr.dush.mediamanager.dto.media.video.VideoFile;

/**
 * File name, after it has been parsed.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
@NoArgsConstructor
@ToString(of = { "movieName", "year", "videoFile" })
public class MoviesParsedName {

	/** Original file name */
	private Path file;

	/** Movie name (after useless data removed from filename) */
	private String movieName;

	/** If it's defined in file name, movie release date */
	private int year = 0;

	/** File extension */
	private String extension;

	private VideoFile videoFile;

	/**
	 * Initialize with original name
	 *
	 * @param file Concerned file
	 */
	public MoviesParsedName(Path file) {
		this.file = file;
	}

	/**
	 * Initialize with final name and date.
	 * @param file Concerned file
	 * @param movieName Final movies name, with space as word separator.
	 * @param year 0 if not defined.
	 */
	public MoviesParsedName(Path file, String movieName, int year) {
		this(file);
		this.movieName = movieName;
		this.year = year;
	}

}
