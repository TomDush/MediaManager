package fr.dush.mediamanager.domain.media;

import fr.dush.mediamanager.domain.media.video.Person;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Only reference to media isn't enough: we're not using relational database. This class contains basic data about a
 * media to be used without lookup full media.
 *
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor
public class MediaSummary extends MediaReference {

    private String title;
    private String poster;

    private double voteAverage;
    private Date release;
    private String tagline;

    private int seen = 0;

    private Set<String> genres = new HashSet<>();
    private List<Person> mainActors = new ArrayList<>();

    public MediaSummary(MediaType mediaType, String id) {
        super(mediaType, id);
    }
}
