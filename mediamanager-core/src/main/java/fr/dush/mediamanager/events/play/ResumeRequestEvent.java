package fr.dush.mediamanager.events.play;

import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.events.AbstractEvent;
import lombok.Data;

/** Request to play a given movie */
@Data
public class ResumeRequestEvent extends AbstractEvent {

    private MediaReference reference;

    public ResumeRequestEvent(Object source, MediaReference reference) {
        super(source);
        this.reference = reference;
    }
}
