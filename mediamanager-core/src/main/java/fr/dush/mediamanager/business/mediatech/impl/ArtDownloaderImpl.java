package fr.dush.mediamanager.business.mediatech.impl;

import com.google.common.hash.Hashing;
import fr.dush.mediamanager.annotations.Config;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.business.mediatech.ArtRepository;
import fr.dush.mediamanager.business.mediatech.IArtDownloader;
import fr.dush.mediamanager.domain.media.art.Art;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import fr.dush.mediamanager.domain.media.art.ArtType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.io.Files.*;
import static org.apache.commons.lang3.StringUtils.*;

@Named
public class ArtDownloaderImpl implements IArtDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtDownloaderImpl.class);

    @Inject
    @Config(name = "Mediatech", definition = "configuration/paths.json")
    private ModuleConfiguration configuration;

    private Path temp;

    private Path imageRootPath;

    /**
     * Read the configuration once.
     */
    @PostConstruct
    public void readConfiguration() {
//        imageRootPath = Paths.get(configuration.readValue("downloader.imagespath"));
//
//        try {
//            temp = Files.createTempDirectory("MM_Downloader");
//        } catch (IOException e) {
//            throw new ConfigurationException("Can't create new temporary directory.", e);
//        }

        LOGGER.info("ArtDownloaderImpl is configured with : imagespath = {} ; temp = {}", imageRootPath, temp);

//        imageRootPath.toFile().mkdirs();
//        for (ArtType t : ArtType.values()) {
//            imageRootPath.resolve(getPath(t)).toFile().mkdirs();
//        }
//        imageRootPath.resolve(getPath((ArtType) null)).toFile().mkdirs();
    }

    @Override
    public boolean readImage(Art art, ArtQuality artQuality, OutputStream outputStream) throws IOException {
        Path artPath = resolveLocalPath(art, artQuality);

        if (artPath != null) {
            // If it found, copy file to output stream
            Files.copy(artPath, outputStream);
            return true;
        }

        return false;
    }

    @Override
    public void downloadArt(ArtRepository artRepository, Art art, ArtQuality... qualities) throws IOException {
        // Check if file doesn't already exist for this quality...
        for (ArtQuality quality : qualities) {
            Path path = resolveLocalPath(art, quality);
            if (path == null) {

                // Download file to temporary file
                final Path tempFile = createNewTempPath();
                if (artRepository.readImage(art.getRef(), quality, new FileOutputStream(tempFile.toFile()))) {

                    // Move to real directory if not already existing, rename it with hash
                    final Path downloadedFile = imageRootPath.resolve(getPath(art.getType()))
                                                             .resolve(newFinalName(tempFile.toFile(),
                                                                                   escape(art.getShortDescription()),
                                                                                   getFileExtension(art.getRef())));

                    if (!downloadedFile.toFile().exists()) {
                        LOGGER.debug("{} moved into {}", art.getRef(), downloadedFile);
                        Files.move(tempFile, downloadedFile);

                    } else {
                        LOGGER.warn("The file {} has been downloaded 2 times! [art: {}]", downloadedFile, art);
                    }

                    // Fill art
                    art.getDownloadedFiles().put(quality, getWebRelativeUrl(downloadedFile));
                }
            }
        }

    }

    private Path resolveLocalPath(Art art, ArtQuality artQuality) {
        String fileName = art.getDownloadedFiles().get(artQuality);

        if (isNotEmpty(fileName)) {
            Path path = imageRootPath.resolve(fileName);
            if (path.toFile().isFile()) {
                return path;
            }
        }

        return null;
    }

    private String getWebRelativeUrl(final Path downloadedFile) {
        return imageRootPath.relativize(downloadedFile).toString().replace("\\", "/");
    }

    /**
     * Generate unique temporary file path.
     */
    private Path createNewTempPath() {
        return temp.resolve(Long.toString(System.currentTimeMillis()));
    }

    /**
     * Remove all non authorized chars.
     */
    private static String escape(String basename) {
        if (isEmpty(basename)) {
            return "";
        }

        return basename.replaceAll("\\W", "_").replaceAll("_+", "_");
    }

    private static String getPath(ArtType artType) {
        if (artType == null) {
            return getPath(ArtType.OTHER);
        }

        return artType.toString().toLowerCase() + "s";
    }

    /**
     * Generate file name with file hash
     *
     * @param file      File to hash
     * @param basename  Base name to use (if it's blank, use simplified file's basename)
     * @param extension Extension to use, if it's blank, use file's extension.
     * @return File name with hash to avoid overwrite on existing files with same name but not same content.
     */
    private static String newFinalName(final File file, String basename, String extension) throws IOException {
        final StringBuffer sb = new StringBuffer();

        if (isNotBlank(basename)) {
            sb.append(getSimpleFileName(basename));
        } else {
            sb.append(getSimpleFileName(file.toString()));
        }
        sb.append("_").append(hash(file, Hashing.sha1())).append(".");
        if (isNotBlank(extension)) {
            sb.append(extension);
        } else {
            sb.append(getFileExtension(file.toString()));
        }

        return sb.toString();
    }

    /**
     * Get file name without extension, brackets, dash and point.
     */
    public static String getSimpleFileName(final String filePath) {
        final String filename = getNameWithoutExtension(filePath);

        return filename.replaceAll("\\(.*\\)", "")
                       .replaceAll("[ -.]", "_")
                       .replaceAll("_+", "_")
                       .replaceAll("^_|_$", "");
    }

}
