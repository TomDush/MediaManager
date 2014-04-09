package fr.dush.mediacenters.modules.webui.rest.dto;

import lombok.Data;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dush.mediamanager.domain.media.video.Movie;

/**
 * This is an extension of Movie to add some information like if the Movie has been started (recovering)...
 */
@Data
public class MovieDTO extends Movie {

    private RecoveryDTO recovery;

    /** Property added to REST services */
    @JsonProperty("id")
    public ObjectId getAliasId() {
        return getId();
    }

    public void setAliasId(ObjectId id) {}
}
