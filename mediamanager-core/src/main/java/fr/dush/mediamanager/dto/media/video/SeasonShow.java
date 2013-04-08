package fr.dush.mediamanager.dto.media.video;

import static com.google.common.collect.Lists.*;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Show's season.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
public class SeasonShow implements Serializable{

	/** Season number */
	private int seasonNumber;

	/** First episode's release date */
	private String released;

	/** Season name, if different from Show */
	private String specificName;

	/** Ordered episodes */
	private List<EpisodeFile> videoFiles = newArrayList();

}
