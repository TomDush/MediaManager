package fr.dush.mediamanager.dao.media.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Used when returned list it just a sample of full list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedList<T> {

    /** Number of total elements */
    private long fullSize;

    /** Number of object present before the first element of the list */
    private int skipped = 0;

    /** Expected list size */
    private int maxSize = 0;

    /** Requested list */
    private List<T> list = new ArrayList<>();
}
