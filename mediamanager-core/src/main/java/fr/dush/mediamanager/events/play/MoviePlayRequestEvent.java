package fr.dush.mediamanager.events.play;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Thomas Duchatelle
 */
@Data
@AllArgsConstructor
public class MoviePlayRequestEvent {

    private String movieId;

    /** First file path to play, used only as video ID because could be multi-part movie. */
    private String fileId;

}
