package fr.dush.mediamanager.business.mediatech;

import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Thomas Duchatelle
 */
public interface ArtRepository {

    boolean readImage(String artRef, ArtQuality artQuality, OutputStream outputStream) throws IOException;

    Art getMetaData(String artRef);
}
