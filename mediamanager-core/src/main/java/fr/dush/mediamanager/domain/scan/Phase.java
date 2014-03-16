package fr.dush.mediamanager.domain.scan;

import java.io.Serializable;

public enum Phase implements Serializable {
    /** First step ... */
    INIT,

    /** Scanning files, parse names */
    SCANNING,

    /** Get from web missing data on medias. */
    ENRICH,

    /** Scan finish with success */
    SUCCED,

    /** Failed to finish process */
    FAILED, DOWNLOADING;

}