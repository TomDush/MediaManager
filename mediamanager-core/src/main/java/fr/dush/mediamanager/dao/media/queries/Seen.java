package fr.dush.mediamanager.dao.media.queries;

import java.io.Serializable;

/** @author Thomas Duchatelle */
public enum Seen implements Serializable {

    /** All medias */
    ALL,

    /** Only seen medias */
    SEEN,

    /** Only unseen medias */
    UNSEEN;
}
