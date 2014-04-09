package fr.dush.mediamanager.domain.media;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.jongo.marshall.jackson.oid.Id;

import fr.dush.mediamanager.annotations.mapping.DBCollection;

/**
 * @author Thomas Duchatelle
 */
@Data
@DBCollection("Recoveries")
@NoArgsConstructor
public class Recovery {

    /** Media reference and media basic metadata (Title, poster, ...). This is used as ID. */
    private MediaSummary mediaSummary;

    /** Last known position (second) */
    private long position;

    /** Full length (second) */
    private long length;

    /** Media file in reading */
    private List<String> mediaFiles;

    public Recovery(MediaSummary mediaSummary) {
        this.mediaSummary = mediaSummary;
    }

    /** Recovery ID is computed from linked media . */
    @Id
    public MediaReference getId() {
        return new MediaReference(mediaSummary.getMediaType(), mediaSummary.getId());
    }
}
