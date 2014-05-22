package fr.dush.mediamanager.business.mediatech.impl;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;
import fr.dush.mediamanager.engine.mock.MockedConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class ArtDownloaderImplTest {

    public static final String ART_REF = "ART_REF";
    public static final byte[] ART_CONTENT = "This is the file content".getBytes();
    public static final ArtQuality ART_QUALITY = ArtQuality.MINI;

    @InjectMocks
    private ArtDownloaderImpl artDownloader;

    @Mock
    private ArtRepository artRepository;

    @Spy
    private ModuleConfiguration configuration =
            new MockedConfiguration("imagespath", "target/", "trailerpath", "target/");

    @Before
    public void initMockito() throws IOException {
        MockitoAnnotations.initMocks(this);
        artDownloader.readConfiguration();
    }

    @Test
    public void testDownloadArt_OK() throws Exception {
        when(artRepository.readImage(eq(ART_REF),
                                     eq(ART_QUALITY),
                                     any(OutputStream.class))).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OutputStream stream = (OutputStream) invocation.getArguments()[2];
                stream.write(ART_CONTENT);
                return true;
            }
        });

        // Exec
        Art art = new Art(ART_REF);
        art.setType(ArtType.POSTER);
        artDownloader.downloadArt(artRepository, art, ART_QUALITY);

        // Check...
        assertThat(art.getDownloadedFiles()).containsKey(ART_QUALITY);

        String fileName = art.getDownloadedFiles().get(ART_QUALITY);
        Path artPath = Paths.get("target/", fileName);

        assertThat(artPath.toFile()).exists();
        assertThat(Files.readAllBytes(artPath)).isEqualTo(ART_CONTENT);
    }

    /** artRepository will return false: image not downloaded... */
    @Test
    public void testDownloadArt_KO() throws Exception {
        // Exec
        Art art = new Art(ART_REF);
        art.setType(ArtType.POSTER);
        artDownloader.downloadArt(artRepository, art, ART_QUALITY);

        // Check...
        assertThat(art.getDownloadedFiles()).doesNotContainKey(ART_QUALITY);
    }

    @Test
    public void testGetSimpleName() throws Exception {
        assertThat(ArtDownloaderImpl.getSimpleFileName(
                "Crystallize - Lindsey Stirling (Dubstep Violin Original Song).webm")).isEqualTo(
                "Crystallize_Lindsey_Stirling");
    }
}
