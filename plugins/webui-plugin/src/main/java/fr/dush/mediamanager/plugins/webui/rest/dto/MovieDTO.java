package fr.dush.mediamanager.plugins.webui.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dush.mediamanager.domain.media.video.Movie;
import lombok.Data;
import org.bson.types.ObjectId;

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

    public void setAliasId(ObjectId id) {
    }
}
