package fr.dush.mediamanager.business.mediatech;

import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Thomas Duchatelle
 */
public interface IArtDownloader {

    boolean readImage(Art art, ArtQuality artQuality, OutputStream outputStream) throws IOException;

    void downloadArt(ArtRepository artRepository, Art art, ArtQuality... qualities) throws IOException;
}
