package fr.dush.mediamanager.domain.media.art;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jongo.marshall.jackson.oid.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Duchatelle
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"ref"})
public class Art {

    /**
     * URL format is: <code>protocol/identifier</code>.
     * <p/>
     * <ul> <li>protocol is used to determine which ArtRepository to use</li> <li>identifier should be enough to resolve
     * the image</li> </ul>
     */
    @Id
    private String ref;

    private ArtType type;

    /** Short description used to named file */
    private String shortDescription;

    private Map<ArtQuality, String> downloadedFiles = new HashMap<>();

    public Art(String ref) {
        setRef(ref);
    }

}
