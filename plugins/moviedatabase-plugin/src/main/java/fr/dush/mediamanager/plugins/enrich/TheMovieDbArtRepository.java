package fr.dush.mediamanager.plugins.enrich;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.business.mediatech.ArtRepositoryRegisterEvent;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;

/**
 * @author Thomas Duchatelle
 */
@Named
public class TheMovieDbArtRepository implements ArtRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDbArtRepository.class);

    @Inject
    private TheMovieDbApi api;

    // Used only by postConstruct
    @Inject
    private EventBus eventBus;

    private static final Map<String, String> qualities = new HashMap<>();

    static {
        qualities.put(getKey(ArtType.ACTOR, ArtQuality.MINI), "w45");
        qualities.put(getKey(ArtType.ACTOR, ArtQuality.THUMBS), "w185");
        qualities.put(getKey(ArtType.ACTOR, ArtQuality.DISPLAY), "h632");

        qualities.put(getKey(ArtType.POSTER, ArtQuality.MINI), "w92");
        qualities.put(getKey(ArtType.POSTER, ArtQuality.THUMBS), "w185");
        qualities.put(getKey(ArtType.POSTER, ArtQuality.DISPLAY), "w342");

        qualities.put(getKey(ArtType.BACKDROP, ArtQuality.MINI), "w300");
        qualities.put(getKey(ArtType.BACKDROP, ArtQuality.THUMBS), "w300");
        qualities.put(getKey(ArtType.BACKDROP, ArtQuality.DISPLAY), "w780");
    }

    private static String getKey(ArtType type, ArtQuality quality) {
        return type.toString() + quality;
    }

    @PostConstruct
    public void register() {
        eventBus.post(new ArtRepositoryRegisterEvent(TheMovieDbEnricher.MOVIEDB_ID_TYPE, this));
    }

    @Override
    public boolean readImage(String artRef, ArtQuality artQuality, OutputStream outputStream) throws IOException {
        LOGGER.info("Read image {} [quality={}]", artRef, artQuality);
        TheMovieDBArtUrl ref = new TheMovieDBArtUrl(artRef);

        try {
            URL imageUrl = api.createImageUrl(ref.getPath(), getQuality(artQuality, ref.getType()));

            IOUtils.copy(imageUrl.openStream(), outputStream);
            return true;

        }
        catch (MovieDbException e) {
            throw new IOException("Could not resolve art path: " + ref, e);

        }
    }

    private String getQuality(ArtQuality artQuality, ArtType type) {
        if (artQuality == ArtQuality.ORIGINAL) {
            return "original";
        }

        String size = qualities.get(getKey(type, artQuality));

        if (isEmpty(size)) {
            LOGGER.warn("Art quality {} is not supported for {} type. Using ORIGINAL.", artQuality, type);
            return "original";
        }

        return size;
    }

    @Override
    public Art getMetaData(String artRef) {
        return new TheMovieDBArtUrl(artRef).getArt();
    }
}
