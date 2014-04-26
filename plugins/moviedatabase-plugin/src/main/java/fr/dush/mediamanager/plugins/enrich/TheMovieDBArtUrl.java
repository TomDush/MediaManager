package fr.dush.mediamanager.plugins.enrich;

import com.google.common.base.Splitter;
import fr.dush.mediamanager.business.utils.ArtUrlBuilder;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtType;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Facilities to construct and de-serialise art ref.
 */
@Getter
@ToString
public class TheMovieDBArtUrl extends ArtUrlBuilder {

    /** Path must start by / for MovieDB API, it's removed when generate URL add added when retrieve path. */
    private String path;

    private String description;

    public TheMovieDBArtUrl(ArtType type, String path, String description) {
        super(type);

        if (isEmpty(path)) {
            throw new IllegalArgumentException("path must not be empty.");
        }
        this.path = path;
        this.description = isEmpty(description) ? "" : description;
    }

    /**
     * Parse ref to retrieve data
     *
     * @param artRef Ex: themoviedb/poster/ironman/img/qwerty789.jpg'
     */
    public TheMovieDBArtUrl(String artRef) {
        super(artRef);

        ArrayList<String> list = newArrayList(Splitter.on("/").limit(4).split(artRef));
        if (list.size() < 4) {
            throw new IllegalArgumentException("'" + artRef + "' is invalid ref: need at least 4 part (/ separated).");
        }

        this.path = list.get(3).startsWith("/") ? list.get(3) : "/" + list.get(3);
        this.description = list.get(2);
    }

    @Override
    public String getRef() {
        return buildRef(cleanUrl(description), path.startsWith("/") ? path.substring(1) : path);
    }

    @Override
    protected String getRepositoryId() {
        return TheMovieDbEnricher.MOVIEDB_ID_TYPE;
    }

    @Override
    public Art getArt() {
        Art art = super.getArt();
        art.setShortDescription(description);

        return art;
    }
}
