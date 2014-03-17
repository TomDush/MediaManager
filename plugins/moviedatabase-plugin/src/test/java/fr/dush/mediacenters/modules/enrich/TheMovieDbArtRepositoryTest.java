package fr.dush.mediacenters.modules.enrich;

import com.omertron.themoviedbapi.TheMovieDbApi;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Thomas Duchatelle
 */
@RunWith(MockitoJUnitRunner.class)
public class TheMovieDbArtRepositoryTest {

    public static final String REF = "poster/themoviedb/ironman-movie/somewhere/here/ironman.jpg";

    @InjectMocks
    private TheMovieDbArtRepository repository;

    @Mock
    private TheMovieDbApi api;

    @Test
    public void testGetMetaData() throws Exception {
        Art metaData = repository.getMetaData(REF);

        assertThat(metaData.getRef()).isEqualTo(REF);
        assertThat(metaData.getShortDescription()).isEqualTo("ironman-movie");
        assertThat(metaData.getType()).isEqualTo(ArtType.POSTER);
    }

    @Test
    public void testReadImage() throws Exception {

        Path filePath = Paths.get("src/test/resources/log4j.properties");
        when(api.createImageUrl(anyString(), anyString())).thenReturn(filePath.toUri().toURL());

        // Exec
        File file = new File("target/fileCopy.jpg");
        file.delete();

        FileOutputStream outputStream = new FileOutputStream(file);
        repository.readImage(REF, ArtQuality.THUMBS, outputStream);

        // Checks...
        verify(api).createImageUrl("/somewhere/here/ironman.jpg", "w185");
        assertThat(file).hasContentEqualTo(filePath.toFile());
    }
}
