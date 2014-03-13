package fr.dush.mediacenters.modules.webui.rest.dto;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fr.dush.mediamanager.dao.media.queries.MediaField;
import fr.dush.mediamanager.dao.media.queries.Order;
import fr.dush.mediamanager.dao.media.queries.Seen;
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

    /** Expected not null or empty fields */
    private Set<MediaField> notNullFields = new HashSet<>();

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

    @QueryParam("notNullFields")
    public void setNotNullFields(String notNullFields) {
        if (notNullFields == null) {
            this.notNullFields.clear();
        } else {
            this.notNullFields = Sets.newHashSet(Iterables.transform(Splitter.on(',').split(notNullFields),
                                                                     new Function<String, MediaField>() {

                                                                         @Override
                                                                         public MediaField apply(String input) {
                                                                             return MediaField.valueOf(input.toUpperCase());
                                                                         }
                                                                     }));
        }
    }
}
