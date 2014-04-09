package fr.dush.mediamanager.dao.mediatech.mongodb;

import fr.dush.mediamanager.dao.mediatech.IRecoveryDAO;
import fr.dush.mediamanager.dao.mongodb.AbstractDAO;
import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.domain.media.Recovery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RecoveryDAOImpl extends AbstractDAO<Recovery, MediaReference> implements IRecoveryDAO {

    @Inject
    public RecoveryDAOImpl() {
        super(Recovery.class);
    }

}
