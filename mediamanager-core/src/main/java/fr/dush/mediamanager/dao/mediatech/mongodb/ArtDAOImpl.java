package fr.dush.mediamanager.dao.mediatech.mongodb;

import fr.dush.mediamanager.dao.mediatech.IArtDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.domain.media.art.Art;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ArtDAOImpl extends AbstractDAO<Art, String> implements IArtDAO {

    public ArtDAOImpl() {
        super(Art.class);
    }

}
