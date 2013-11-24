package fr.dush.mediacenters.modules.webui.rest.dto;

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
