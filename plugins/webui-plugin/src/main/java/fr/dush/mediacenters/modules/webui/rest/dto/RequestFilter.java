package fr.dush.mediacenters.modules.webui.rest.dto;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import lombok.Data;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Research form filter
 *
 * @author Thomas Duchatelle
 */
@Data
public class RequestFilter implements Serializable {

    /** Order to use, default ALPHA. */
    private Order order;

    /** Filter on media already seen, or not */
    private Seen seen;

    /** Media title or global research */
    @QueryParam("title")
    private String title;

    /** Genres to match ... */
    private Set<String> genres = new HashSet<>();

    /** If expect max number of element. If different of 0, disable pagination feature. */
    @QueryParam("size")
    @DefaultValue("0")
    private int size;

    /** Paginate results, it's the default behavior */
    @Form
    private Pagination pagination;

    @QueryParam("genres")
    public void setGenres(String genres) {
        if (genres == null) {
            this.genres.clear();
        } else {
            this.genres = Sets.newHashSet(Splitter.on(',').split(genres));
        }
    }

    @PathParam("order")
    @DefaultValue("ALPHA")
    public void setOrder(String value) {
        if (value != null) {
            this.order = Order.valueOf(value.toUpperCase());
        }
    }

    @QueryParam("seen")
    @DefaultValue("ALL")
    public void setSeen(String value) {
        if (value != null) {
            this.seen = Seen.valueOf(value.toUpperCase());
        }
    }
}
