package fr.dush.mediamanager.business.mediatech.impl;

import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.business.mediatech.ArtRepositoryRegisterEvent;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.dao.mediatech.IArtDAO;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.engine.SimpleJunitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class ArtManagerImplTest extends SimpleJunitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtManagerImplTest.class);
    public static final byte[] ART_CONTENT = "Hello World!".getBytes();
    public static final String ART_REF = "junitart/myImage.jpg";

    @InjectMocks
    private ArtManagerImpl artManager;

    @Mock
    private IArtDAO artDAO;
    @Mock
    private IArtDownloader artDownloader;
    @Mock
    private ArtRepository artRepository;

    @Before
    public void initMocks() throws Exception {
        artManager.registerArtRepository(new ArtRepositoryRegisterEvent("junitart", artRepository));

        when(artRepository.readImage(eq(ART_REF),
                                     eq(ArtQuality.ORIGINAL),
                                     any(OutputStream.class))).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = (OutputStream) invocation.getArguments()[2];
                os.write(ART_CONTENT);

                return true;
            }
        });
    }

    @Test
    public void testReadImage_DOESNT_EXIST() throws Exception {
        // Exec ...
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        artManager.readImage(ART_REF, ArtQuality.ORIGINAL, outputStream);

        // Check
        assertThat(outputStream.toByteArray()).isEqualTo(ART_CONTENT);
        verifyZeroInteractions(artDownloader);
    }

    @Test
    public void testReadImage_EXISTS() throws Exception {
        reset(artRepository);

        Art art = new Art(ART_REF);
        when(artDAO.findById(ART_REF)).thenReturn(art);
        when(artDownloader.readImage(any(Art.class),
                                     any(ArtQuality.class),
                                     any(OutputStream.class))).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = (OutputStream) invocation.getArguments()[2];
                os.write(ART_CONTENT);

                return true;
            }
        });

        // Exec ...
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        artManager.readImage(ART_REF, ArtQuality.ORIGINAL, outputStream);

        // Check
        assertThat(outputStream.toByteArray()).isEqualTo(ART_CONTENT);

        verify(artDownloader).readImage(eq(art), eq(ArtQuality.ORIGINAL), any(OutputStream.class));

        verifyZeroInteractions(artRepository);
    }

    @Test
    public void testWriteImage() throws Exception {
        when(artRepository.getMetaData(anyString())).thenReturn(new Art(ART_REF));

        // Exec ...
        Art art = artManager.downloadArt(ART_REF, ArtQuality.ORIGINAL);

        // Check
        verify(artDAO).findById(ART_REF);
        verify(artDAO).save(art);

        verify(artDownloader).downloadArt(artRepository, art, ArtQuality.ORIGINAL);

        verifyNoMoreInteractions(artDAO, artDownloader);
    }
}
