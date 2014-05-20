package fr.dush.mediamanager.domain.media;

import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.domain.media.video.Person;

/**
 * Only reference to media isn't enough: we're not using relational database. This class contains basic data about a
 * media to be used without lookup full media.
 * 
 * @author Thomas Duchatelle
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
