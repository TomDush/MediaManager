package fr.dush.mediamanager.dto.media.video;

import java.io.Serializable;

import lombok.Data;
import fr.dush.mediamanager.dto.media.Sources;

/**
 * "Link" to {@link MoviesCollection} saved in {@link Movie}.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
public class BelongToCollection implements Serializable {

	/** Collection IDs */
	private Sources mediaIds = new Sources();

	/** Number of movie in this collection */
	private int part;

	/** Total part in this collection */
	private int totalPart;

	/** Collection title */
	private String title;

}
