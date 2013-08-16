package fr.dush.mediamanager.business.mediatech;

import static com.google.common.io.Files.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.axet.vget.VGet;
import com.google.common.hash.Hashing;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

@ApplicationScoped
public class ArtDownloaderImpl implements IArtDownloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtDownloaderImpl.class);

	@Inject
	@Configuration(name = "Mediatech", definition = "configuration/mediatech.json")
	private ModuleConfiguration configuration;

	private Path temp;

	private Path imageRootPath;

	private Path trailersRootPath;

	public ArtDownloaderImpl() throws IOException {
		temp = Files.createTempDirectory("MM_Downloader");

		LOGGER.info("ArtDownloaderImpl is configured with : imagespath = {} ; trailerpath = {} ; temp = {}", imageRootPath,
				trailersRootPath, temp);
	}

	/**
	 * Read the configuration once.
	 */
	@PostConstruct
	public void readConfiguration() {
		imageRootPath = FileSystems.getDefault().getPath(configuration.readValue("downloader.imagespath"));
		trailersRootPath = FileSystems.getDefault().getPath(configuration.readValue("downloader.trailerpath"));
	}

	@Override
	public String storeImage(URL url, String basename) {
		try {
			// Download to temporary directory
			final Path tempFile = createNewTempPath();
			Files.copy(url.openStream(), tempFile);
			LOGGER.debug("{} donwloaded into {}", url, tempFile);

			// Move to real directory if not already existing, rename it with hash
			final Path downloadedFile = imageRootPath.resolve(newFinalName(tempFile.toFile(), basename, getFileExtension(url.getFile())));
			if (!downloadedFile.toFile().exists()) {
				LOGGER.debug("{} moved into {}", url, downloadedFile);
				Files.move(tempFile, downloadedFile);
			}

			// Return relative file path
			return imageRootPath.relativize(downloadedFile).toString();

		} catch (IOException e) {
			LOGGER.error("Error while downloading file {}.", url, e);
			return null;
		}

	}

	/**
	 * Generate unique temporary file path.
	 *
	 * @return
	 */
	protected Path createNewTempPath() {
		return temp.resolve(Long.toString(System.currentTimeMillis()));
	}

	@Override
	public Path getImagePath(String relativePath) {
		return imageRootPath.resolve(relativePath);
	}

	@Override
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
	 * @param file File to hash
	 * @param basename Base name to use (if it's blank, use simplified file's basename)
	 * @param extension Extension to use, if it's blank, use file's extension.
	 * @return File name with hash to avoid overwrite on existing files with same name but not same content.
	 * @throws IOException
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
	 *
	 * @param filePath
	 * @return
	 */
	public static String getSimpleFileName(final String filePath) {
		final String filename = getNameWithoutExtension(filePath);

		return filename.replaceAll("\\(.*\\)", "").replaceAll("[ -.]", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
	}

	@Override
	public Path getTrailerPath(String relativePath) {
		return trailersRootPath.resolve(relativePath);
	}

}
