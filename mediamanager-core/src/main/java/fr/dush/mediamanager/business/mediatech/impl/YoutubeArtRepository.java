package fr.dush.mediamanager.business.mediatech.impl;

import com.github.axet.vget.VGet;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.io.Files.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * DEAD CODE!!
 */
public class YoutubeArtRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtDownloaderImpl.class);

    private Path temp;

    private Path trailersRootPath;

    public String storeTrailer(URL trailer, String basename) {
        try {
            // Download trailer into temporary directory, use default filename
            VGet vget = new VGet(trailer, temp.toFile());
            vget.download();
            final File originalName = vget.getTarget();
            LOGGER.info("{} downloaded into : {}", trailer, originalName);

            // Move trailer to real directory
            final Path finalPath = trailersRootPath.resolve(newFinalName(originalName, basename, null));
            if (!finalPath.toFile().exists()) {
                Files.move(originalName.toPath(), finalPath);
            }

            return trailersRootPath.relativize(finalPath).toString();

        } catch (IOException e) {
            LOGGER.error("Can't donwload trailer {}.", trailer, e);
            return null;
        }
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
