package fr.dush.mediamanager.business.utils;

import com.google.common.base.Joiner;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtType;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author Thomas Duchatelle
 */
@Getter
public abstract class ArtUrlBuilder {

    private ArtType type;

    protected ArtUrlBuilder(ArtType type) {
        this.type = type;
    }

    public ArtUrlBuilder(String artRef) {
        type = readType(artRef);
    }

    public Art getArt() {
        Art art = new Art(getRef());
        art.setType(type);

        return art;
    }

    protected String buildRef(String... elements) {
        return Joiner.on("/").join(type.toString().toLowerCase(), getRepositoryId(), elements);
    }

    public abstract String getRef();

    protected abstract String getRepositoryId();

    public static ArtType readType(String artRef) {
        if (isEmpty(artRef) || !artRef.contains("/")) {
            throw new IllegalArgumentException("artRef must not be empty and must contains at least one slash '/'.");
        }

        return ArtType.valueOf(artRef.substring(0, artRef.indexOf('/')).toUpperCase());
    }

    public static String cleanUrl(String description) {
        return description.replaceAll("\\W", "-").replaceAll("[_-]+", "-").replaceAll("(^-+|-+$)", "").toLowerCase();
    }

    public static String getArtRepositoryId(String artRef) {
        int nextFirstSlash = artRef.indexOf("/") + 1;
        return artRef.substring(nextFirstSlash, artRef.indexOf('/', nextFirstSlash));
    }
}
