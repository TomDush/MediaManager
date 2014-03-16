package fr.dush.mediamanager.dao.mediatech.mongodb;

import fr.dush.mediamanager.dao.mediatech.IArtDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.domain.media.art.Art;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArtDAOImpl extends AbstractDAO<Art, String> implements IArtDAO {

    @Inject
    public ArtDAOImpl() {
        super(Art.class);
    }

}
