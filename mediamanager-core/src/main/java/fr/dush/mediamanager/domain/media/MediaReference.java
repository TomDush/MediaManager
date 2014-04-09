package fr.dush.mediamanager.domain.media;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;

/**
 * A full reference to a media
 * 
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaReference implements Serializable {

    /** Media type (collection) */
    private MediaType mediaType;

    /** Identifier of this media */
    private String id;

    public MediaReference(MediaType mediaType, ObjectId id) {
        if (id == null || isEmpty(id.toString())) {
            throw new IllegalArgumentException("Id must not be null or empty. Was: " + id);
        }

        this.mediaType = mediaType;
        this.id = id.toString();
    }
}
