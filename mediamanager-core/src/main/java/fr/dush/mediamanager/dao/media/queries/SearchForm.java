package fr.dush.mediamanager.dao.media.queries;

import com.google.common.collect.Sets;
import fr.dush.mediamanager.domain.media.SourceId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Research form filter
 *
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor
public class SearchForm implements Serializable {

    /** Filter on media already seen, or not */
    private Seen seen;

    /** Media title or global research */
    private String title;

    /** Genres to match ... */
    private Set<String> genres = new HashSet<>();

    /** Identifier from other site */
    private Set<SourceId> mediaIds;

    /** Crew identifier from other site */
    private Set<SourceId> crewIds;

    /** Expected not null or empty fields */
    private Set<MediaField> notNullFields = new HashSet<>();

    public SearchForm(String title) {
        this.title = title;
    }

    public SearchForm(Seen seen) {
        this.seen = seen;
    }

    public SearchForm(String... genres) {
        this.genres = Sets.newHashSet(genres);
    }

}
