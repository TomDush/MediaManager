package fr.dush.mediamanager.dao.media.queries;

import lombok.Data;

import java.io.Serializable;

/**
 * Information on requested page.
 *
 * @author Thomas Duchatelle
 */
@Data
public class SearchLimit implements Serializable {

    /** Index page, BE CAREFUL, <b>starting by 1</b> ! 0 is default value to disable pagination... */
    private int index;

    /** Page size, default is 10 */
    private int pageSize;

    /** Max element number, 0 is default value to enable pagination... */
    private int maxSize = 0;

    public boolean isPaginationActive() {
        return isMaxSizeDefined() && index > 0;
    }

    public boolean isMaxSizeDefined() {
        return maxSize > 0;
    }
}
