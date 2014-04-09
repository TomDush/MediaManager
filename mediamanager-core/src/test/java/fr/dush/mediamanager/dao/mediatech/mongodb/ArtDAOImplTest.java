package fr.dush.mediamanager.dao.mediatech.mongodb;

import fr.dush.mediamanager.dao.mediatech.IArtDAO;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;
import fr.dush.mediamanager.domain.media.Assertions;
import fr.dush.mediamanager.engine.MongoJunitTest;
import org.junit.Test;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * @author Thomas Duchatelle
 */
public class ArtDAOImplTest extends MongoJunitTest {

    public static final String REF = "MY_REF";
    public static final String DESC = "Junit_test";

    @Inject
    private IArtDAO artDAO;

    @Test
    public void testSaveAndLoad() throws Exception {
        Art art = new Art(REF);
        art.setShortDescription(DESC);
        art.setType(ArtType.BACKDROP);
        art.getDownloadedFiles().put(ArtQuality.THUMBS, "a_file/in/filesystem.fs");

        // Save
        artDAO.save(art);

        // Try to reload
        Art reloaded = artDAO.findById(REF);
        HashMap<ArtQuality, String> expectedMap = new HashMap<>();
        expectedMap.put(ArtQuality.THUMBS, "a_file/in/filesystem.fs");
        Assertions.assertThat(reloaded)
                  .hasRef(REF)
                  .hasShortDescription(DESC)
                  .hasType(ArtType.BACKDROP)
                  .hasDownloadedFiles(expectedMap);
    }
}
