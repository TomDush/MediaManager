package fr.dush.mediamanager.dao.media;

import java.util.List;
import java.util.Set;

/**
 * Generic data on medias : statistics, shared collections (genres, ...), ...
 *
 * @author Thomas Duchatelle
 */
public interface IMediaDAO {

    /** Get all used genres */
    Set<String> findAllGenres();
}
