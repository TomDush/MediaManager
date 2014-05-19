package fr.dush.mediamanager.events.play;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.events.AbstractEvent;

/** Request to play a given movie */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResumeRequestEvent extends AbstractEvent {

    private MediaReference reference;

    public ResumeRequestEvent(Object source, MediaReference reference) {
        super(source);
        this.reference = reference;
    }
}
