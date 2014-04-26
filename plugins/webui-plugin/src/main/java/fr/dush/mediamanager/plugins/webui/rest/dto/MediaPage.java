package fr.dush.mediamanager.plugins.webui.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Page returned by REST service.
 *
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor
public class MediaPage<T> implements Serializable {

    /** Index of this page, 0 if pagination isn't available. */
    private int page = 0;

    /** Elements by page */
    private int pageSize = 10;

    /** Number of pages */
    private int number = 0;

    /** Number of available elements */
    private long size = 0;

    /** Elements */
    private List<T> elements = new ArrayList<>();

    public MediaPage(List<T> elements) {
        this.elements = elements;
    }

    public MediaPage(int page, int pageSize, int number, int size, List<T> elements) {
        this.page = page;
        this.pageSize = pageSize;
        this.number = number;
        this.size = size;
        this.elements = elements;
    }
}
