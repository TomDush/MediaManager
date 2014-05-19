package fr.dush.mediamanager.business.mediatech.impl;

import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.business.mediatech.ArtRepositoryRegisterEvent;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.business.mediatech.IArtManager;
import fr.dush.mediamanager.business.utils.ArtUrlBuilder;
import fr.dush.mediamanager.dao.mediatech.IArtDAO;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.tools.RetryApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Named
public class ArtManagerImpl implements IArtManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtManagerImpl.class);

    @Inject
    private IArtDownloader artDownloader;

    @Inject
    private IArtDAO artDAO;

    private Map<String, ArtRepository> repositories = new HashMap<>();

    @Subscribe
    public void registerArtRepository(ArtRepositoryRegisterEvent event) {
        if (repositories.containsKey(event.getName())) {
            throw new ConfigurationException(
                    "Could not register 2 ArtRepository with the same name. Previous was: {}, new one is: {}",
                    repositories.get(event.getName()),
                    event.getArtRepository());
        }

        repositories.put(event.getName(), event.getArtRepository());
    }

    @Override
    public boolean readImage(String artRef, ArtQuality artQuality, OutputStream outputStream) throws IOException {
        LOGGER.info("Read art {} [quality={}]", artRef, artQuality);

        Art art = artDAO.findById(artRef);

        // If found, try to find it in local with art downloader.
        if (art == null || !artDownloader.readImage(art, artQuality, outputStream)) {

            // If not found, read it directly from the source...
            return resolveArtRepository(artRef).readImage(artRef, artQuality, outputStream);
        }

        return true;
    }

    @Override
    public Art downloadArt(String artRef, final ArtQuality... qualities) {
        LOGGER.info("Downloading art {} [qualities={}]", artRef, qualities);

        final ArtRepository artRepository = resolveArtRepository(artRef);

        Art art = artDAO.findById(artRef);
        if (art == null) {
            art = artRepository.getMetaData(artRef);
        }

        // Download art, if not already downloaded...
        final Art finalArt = art;
        RetryApi.retry(new RetryApi.MethodWithReturn<Object>() {
            @Override
            public Object doIt() throws Exception {
                artDownloader.downloadArt(artRepository, finalArt, qualities);
                return null;
            }
        });

        // Save/update this art and return it
        artDAO.save(art);

        return art;
    }

    private ArtRepository resolveArtRepository(String artRef) {
        String protocol = ArtUrlBuilder.getArtRepositoryId(artRef);

        ArtRepository repo = repositories.get(protocol);
        if (repo == null) {
            throw new IllegalArgumentException(String.format("Repository [%s] isn't know. Could not resolve artRef: %s",
                                                             protocol,
                                                             artRef));
        }
        return repo;
    }

}
