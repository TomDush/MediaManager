package fr.dush.mediacenters.modules.enrich;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.business.mediatech.ArtRepositoryRegisterEvent;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author Thomas Duchatelle
 */
@ApplicationScoped
@Startup
public class TheMovieDbArtRepository implements ArtRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbArtRepository.class);

    @Inject
    private TheMovieDbApi api;

    private static final Map<String, String> qualities = new HashMap<>();

    static {
        qualities.put(getKey(ArtType.ACTOR, ArtQuality.MINI), "w45");
        qualities.put(getKey(ArtType.ACTOR, ArtQuality.THUMBS), "w185");

        qualities.put(getKey(ArtType.POSTER, ArtQuality.MINI), "w92");
        qualities.put(getKey(ArtType.POSTER, ArtQuality.THUMBS), "w185");

        qualities.put(getKey(ArtType.BACKDROP, ArtQuality.MINI), "w300");
        qualities.put(getKey(ArtType.BACKDROP, ArtQuality.THUMBS), "w300");
    }

    private static String getKey(ArtType type, ArtQuality quality) {
        return type.toString() + quality;
    }

    @PostConstruct
    @Inject
    public void register(Event<ArtRepositoryRegisterEvent> bus) {
        bus.fire(new ArtRepositoryRegisterEvent(TheMovieDbEnricher.MOVIEDB_ID_TYPE, this));
    }

    @Override
    public boolean readImage(String artRef, ArtQuality artQuality, OutputStream outputStream) throws IOException {
        TheMovieDBArtUrl ref = new TheMovieDBArtUrl(artRef);

        try {
            URL imageUrl = api.createImageUrl(ref.getPath(), getQuality(artQuality, ref.getType()));

            IOUtils.copy(imageUrl.openStream(), outputStream);
            return true;

        } catch (MovieDbException e) {
            throw new IOException("Could not resolve art path: " + ref, e);

        }
    }

    private String getQuality(ArtQuality artQuality, ArtType type) {
        if (artQuality == ArtQuality.ORIGINAL) {
            return "original";
        }

        String size = qualities.get(getKey(type, artQuality));

        return isEmpty(size) ? "original" : size;
    }

    @Override
    public Art getMetaData(String artRef) {
        return new TheMovieDBArtUrl(artRef).getArt();
    }
}
