package fr.dush.mediamanager.business.mediatech;

import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provide tools to download on local hard drive arts around medias : posters, trailers, ...
 *
 * @author Thomas Duchatelle
 */
public interface IArtManager {

    /**
     * Read art where it is and write it into an output stream.
     */
    void readImage(String artRef, ArtQuality artQuality, OutputStream outputStream) throws IOException;

    /**
     * Download art and store it in local.
     *
     * @param artRef  Full reference to art
     * @param quality At least 1 quality to download.
     */
    Art downloadArt(String artRef, ArtQuality... quality);
}
