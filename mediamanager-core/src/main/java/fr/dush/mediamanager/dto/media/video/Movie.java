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
 * Media is movie type.
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class Movie extends Media {

	/** Video files : can have multiple quality, or version ... */
	private Set<VideoFile> videoFiles = newHashSet();

	/** Release date */
	private Date release;

	/** Movie overview */
	private String overview;

	/** If movie belong to collection */
	private BelongToCollection collection;

	/** Screenshot or fan arts to display back to the presentation. */
	private List<String> backdrops = newArrayList();

	/** 1 - 5 main actors */
	private List<Person> mainActors = newArrayList();

	/** Movie director */
	private List<Person> directors = newArrayList();

	/** Movie types : actions, comedy, ... */
	private Set<String> genres = newHashSet();

	/** Trailers (downloaded, and not). Null if not initialized... */
	private Trailers trailers = null;

	@Override
	public String toString() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(getRelease());
		return getTitle() + " (" + calendar.get(Calendar.YEAR) + ")";
	}

	public StringBuffer prettyPrint(StringBuffer sb) {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		if (null == sb) sb = new StringBuffer();

		Function<Person, String> names = new Function<Person, String>() {
			@Override
			public String apply(Person p) {
				return p.getName();
			}
		};

		sb.append("\n\t** ").append(getTitle()).append(" **");
		if (getRelease() != null) sb.append(" (").append(formatter.format(getRelease())).append(")");
		sb.append("\n");

		if (null != collection) {
			sb.append("Belong to collection : ").append(collection.getTitle()).append(", part ").append(collection.getPart())
					.append(" of ").append(collection.getTotalPart()).append("\n");
		}
		sb.append("Genres : ").append(Joiner.on(", ").join(genres)).append("\n");
		if (null != overview) sb.append("Overview : \n\t").append(overview).append("\n");
		sb.append("Director(s) : ").append(Joiner.on(", ").join(Lists.transform(directors, names))).append("\n");
		sb.append("Cast : ").append(Joiner.on(", ").join(Lists.transform(mainActors, names))).append("\n");
		sb.append("Poster : ").append(getPoster()).append("\n");
		if (!backdrops.isEmpty()) sb.append("Backdrops : ").append(Joiner.on("\n\t- ").join(backdrops)).append("\n");
		if (null != trailers) {
			if (trailers.getTrailers().isEmpty()) {
				sb.append("No trailers, refreshed on ").append(formatter.format(trailers.getRefreshed())).append(") : \n");

			} else {
				sb.append("Trailers (refreshed on ").append(trailers.getRefreshed()).append(") : \n");
				for (Trailer t : trailers.getTrailers()) {
					sb.append("\t- ").append(t.getTitle());
					if (t.getPublishDate() != null) sb.append(" (").append(t.getPublishDate()).append(")");
					sb.append(" : ");

					if (t.getTrailer() != null) sb.append(t.getTrailer()).append(" (source = ").append(t.getUrl()).append(")");
					else sb.append(t.getUrl());
					sb.append("\n");
				}
			}
		} else {
			sb.append("Trailers not initilized...");
		}

		return sb;
	}
}
