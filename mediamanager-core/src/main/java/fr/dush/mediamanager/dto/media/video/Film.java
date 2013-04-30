package fr.dush.mediamanager.dto.media.video;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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

	/** Film overview */
	private String overview;

	/** Screenshot or fan arts to display back to the presentation. */
	private List<String> backdrops = newArrayList();

	/** 1 - 5 main actors */
	private List<Person> mainActors = newArrayList();

	/** Film director */
	private List<Person> directors = newArrayList();

	/** Film types : actions, comedy, ... */
	private Set<String> genres = newHashSet();

	/** Downloaded trailers */
	private Set<VideoFile> trailers = newHashSet();

	@Override
	public String toString() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getRelease());
		return getTitle() + " (" + calendar.get(Calendar.YEAR) + ")";
	}

	public StringBuffer prettyPrint(StringBuffer sb) {
		if (null == sb) sb = new StringBuffer();

		Function<Person, String> names = new Function<Person, String>() {
			@Override
			public String apply(Person p) {
				return p.getName();
			}
		};

		sb.append("\n\t** ").append(getTitle()).append(" **");
		if(getRelease() != null) sb.append(" (").append(new SimpleDateFormat("yyyy-MM-dd").format(getRelease())).append(")");
		sb.append("\n");
		sb.append("Genres : ").append(Joiner.on(", ").join(genres)).append("\n");
		if (null != overview) sb.append("Overview : \n\t").append(overview).append("\n");
		sb.append("Director(s) : ").append(Joiner.on(", ").join(Lists.transform(directors, names))).append("\n");
		sb.append("Cast : ").append(Joiner.on(", ").join(Lists.transform(mainActors, names))).append("\n");
		sb.append("Poster : ").append(getPoster()).append("\n");
		if (!backdrops.isEmpty()) sb.append("Backdrops : ").append(Joiner.on("\n\t- ").join(backdrops)).append("\n");
		if (!trailers.isEmpty()) sb.append("Donwloaded trailers : ").append(trailers).append("\n");

		return sb;
	}

}
