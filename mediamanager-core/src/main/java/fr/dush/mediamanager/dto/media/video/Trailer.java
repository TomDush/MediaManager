package fr.dush.mediamanager.dto.media.video;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Movie or show trailer
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(of = "url")
public class Trailer implements Serializable {

	/** Trailer publish date, if any... */
	public Date publishDate;

	/** Trailer title */
	public String title;

	/** Source (web site's name), example : "youtube" */
	public String source;

	/** Maximum available quality*/
	public String quality;

	/** URL on web */
	private String url;

	/** If trailer has been downloaded, local video */
	private VideoFile trailer;

}
