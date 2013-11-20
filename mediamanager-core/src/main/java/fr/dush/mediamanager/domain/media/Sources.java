package fr.dush.mediamanager.domain.media;

import static com.google.common.collect.Collections2.*;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * HasSet extension to provide method utilities to add and get {@link SourceId}.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
public class Sources extends HashSet<SourceId> {

	/** Generic source id : IMDB */
	public static final String IMDB = "imdb";

	/** If no source and using generated internal id */
	public static final String INTERN = "local";

	public void addId(SourceId id) {
		add(id);
	}

	public void addId(String type, String value) {
		add(new SourceId(type, value));
	}

	public Collection<String> getIds(final String type) {
		return transform(filter(this, new Predicate<SourceId>() {
			@Override
			public boolean apply(SourceId id) {
				return type.toLowerCase().equals(id.getType().toLowerCase());
			}

		}), new Function<SourceId, String>() {
			@Override
			public String apply(SourceId id) {
				return id.getValue();
			}

		});
	}

}
