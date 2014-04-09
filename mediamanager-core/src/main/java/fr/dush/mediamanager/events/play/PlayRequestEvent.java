package fr.dush.mediamanager.events.play;

import fr.dush.mediamanager.domain.media.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Request to play a given movie */
@Data
@AllArgsConstructor
public class PlayRequestEvent {

    private MediaType type;

    private String movieId;

    /** First file path to play, used only as video ID because could be multi-part movie. */
    private String fileId;

}
