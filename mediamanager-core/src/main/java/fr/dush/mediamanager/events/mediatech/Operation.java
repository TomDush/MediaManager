package fr.dush.mediamanager.events.mediatech;

/**
 * @author Thomas Duchatelle
 */
public enum Operation {
    /** Mark movie as already viewed */
    MARK_VIEWED,
    /** Mark movie as not viewed */
    MARK_UNVIEWED,

    /** If movie has been started, remove recovery marker */
    REMOVE_RESUME,

    /** Add movie to watch list */
    ADD_WATCH_LIST,

    /** Mark to delete */
    MARK_TO_DELETE;
}
