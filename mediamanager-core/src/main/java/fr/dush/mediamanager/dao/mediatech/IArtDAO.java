package fr.dush.mediamanager.dao.mediatech;

import fr.dush.mediamanager.domain.media.art.Art;

/**
 * @author Thomas Duchatelle
 */
public interface IArtDAO {

    Art findById(String artRef);

    void save(Art art);
}
