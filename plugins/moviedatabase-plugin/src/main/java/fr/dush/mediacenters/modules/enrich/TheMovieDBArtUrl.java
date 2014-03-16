package fr.dush.mediacenters.modules.enrich;

import com.google.common.base.Splitter;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtType;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

import static com.google.common.collect.Lists.*;

/**
 * Facilities to construct and de-serialise art ref.
 */
@Getter
@ToString
public class TheMovieDBArtUrl {

    private ArtType type;

    private String path;

    private String description;

    public TheMovieDBArtUrl(ArtType type, String path, String description) {
        this.type = type;
        this.path = path;
        this.description = description;
    }

    /**
     * Parse ref to retrieve data
     *
     * @param artRef Ex: themoviedb/poster/ironman/img/qwerty789.jpg'
     */
    public TheMovieDBArtUrl(String artRef) {
        ArrayList<String> list = newArrayList(Splitter.on("/").limit(4).split(artRef));
        if (list.size() < 4) {
            throw new IllegalArgumentException("'" + artRef + "' is invalid ref: need at least 4 part (/ separated).");
        }

        this.type = ArtType.valueOf(list.get(1).toUpperCase());
        this.path = list.get(3);
        this.description = list.get(2);
    }

    public String getRef() {
        StringBuilder sb = new StringBuilder(TheMovieDbEnricher.MOVIEDB_ID_TYPE).append("/");
        sb.append(type.toString().toLowerCase()).append("/");
        sb.append(cleanUrl(description)).append("/");
        sb.append(path.startsWith("/") ? path.substring(1) : path);

        return sb.toString();
    }

    private String cleanUrl(String description) {
        return description.replaceAll("\\W", "-").replace("[_-]{2,}", "_").toLowerCase();
    }

    public Art getArt() {
        Art art = new Art(getRef());
        art.setType(type);
        art.setShortDescription(description);

        return art;
    }
}
