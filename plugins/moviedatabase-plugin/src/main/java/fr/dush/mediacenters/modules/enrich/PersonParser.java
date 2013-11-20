package fr.dush.mediacenters.modules.enrich;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.Person;

import fr.dush.mediamanager.business.mediatech.ImageType;

/**
 * Sort movie's casting and crew.
 *
 * @author Thomas Duchatelle
 *
 */
public class PersonParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersonParser.class);

	private static final String ACTOR_JOB = "ACTOR";

	private static final String DIRECTOR_JOB = "DIRECTOR";

	private TheMovieDbEnricher parent;

	private List<Person> persons;

	public PersonParser(TheMovieDbEnricher parent, List<Person> persons) {
		this.parent = parent;
		this.persons = persons;
	}

	/**
	 * Get cast actors.
	 *
	 * @param limit Limit result size. 0 to take all...
	 * @return
	 */
	public List<fr.dush.mediamanager.domain.media.video.Person> getCasting(int limit) {
		// Select and transform actors
		final Collection<fr.dush.mediamanager.domain.media.video.Person> casting = transform(filter(persons, selectPerson(ACTOR_JOB)),
				personConverter);

		// If no limit selected
		if (0 == limit) return newArrayList(casting);

		// Else, limit result size.
		return newArrayList(filter(casting, limit(limit)));
	}

	/**
	 * Select and return directors...
	 *
	 * @return
	 */
	public List<fr.dush.mediamanager.domain.media.video.Person> getDirectors() {
		// Select and transform actors
		final Collection<fr.dush.mediamanager.domain.media.video.Person> directors = transform(filter(persons, selectPerson(DIRECTOR_JOB)),
				personConverter);

		// If no limit selected
		return newArrayList(directors);
	}

	private Function<Person, fr.dush.mediamanager.domain.media.video.Person> personConverter = new Function<Person, fr.dush.mediamanager.domain.media.video.Person>() {

		@Override
		public fr.dush.mediamanager.domain.media.video.Person apply(Person info) {
			fr.dush.mediamanager.domain.media.video.Person person = new fr.dush.mediamanager.domain.media.video.Person();

			person.getSourceIds().addId(TheMovieDbEnricher.MOVIEDB_ID_TYPE, Integer.toString(info.getId()));
			person.setName(info.getName());

			if (isNotBlank(info.getProfilePath())) {
				try {
					person.setPicture(parent.downloadImage(ImageType.ACTOR, info.getProfilePath(), info.getName()));
				} catch (MovieDbException e) {
					LOGGER.warn("Couldn't find person picture from URL {}", info.getProfilePath());
				}
			}

			return person;
		}
	};

	private static Predicate<Person> selectPerson(final String job) {
		return new Predicate<Person>() {

			@Override
			public boolean apply(Person person) {
				return job.equals(null == person.getJob() ? "" : person.getJob().toUpperCase());
			}
		};
	}

	/**
	 * Select only first nb elements.
	 *
	 * @param nb Max number limit.
	 * @return
	 */
	private static <T> Predicate<T> limit(final int nb) {
		return new Predicate<T>() {
			private int i = 0;

			@Override
			public boolean apply(T obj) {
				return i++ < nb;
			}
		};
	}
}
