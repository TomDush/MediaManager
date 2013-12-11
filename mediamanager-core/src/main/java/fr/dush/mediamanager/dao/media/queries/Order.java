package fr.dush.mediamanager.dao.media.queries;

import java.io.Serializable;

/** @author Thomas Duchatelle */
public enum Order implements Serializable {

    /** No specific order, or alphabetical */
    LIST,

    /** Alphabetic order */
    ALPHA,

    /** Inverse alphabetic order */
    ALPHA_DESC,

    /** Recently add in collection first */
    LAST,

    /** First added on top */
    FIRST,

    /** Order by production date : older to newer */
    DATE,

    /** Order by production date : newer to older */
    DATE_DESC,

    /** No importance */
    RANDOM;

}
