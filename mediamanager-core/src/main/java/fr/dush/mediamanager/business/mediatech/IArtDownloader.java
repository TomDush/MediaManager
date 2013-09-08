package fr.dush.mediamanager.business.mediatech;

import java.net.URL;
import java.nio.file.Path;

/**
 * Provide tools to download on local hard drive arts around medias : posters, trailers, ...
 *
 * @author Thomas Duchatelle
 *
 */
public interface IArtDownloader {

	/**
	 * Store in local images (posters, ...) and return relative path which can be saved.
	 *
	 * @param imageType
	 * @param file
	 * @param basename Name of what it represent (movie name, ...)
	 *
	 * @return
	 */
	String storeImage(ImageType imageType, URL file, String basename);

	/**
	 * Download trailer from web site like youtube or vimeo.
	 *
	 * @param trailer
	 * @param basename Name of what it represent (movie name, ...)
	 * @return Relative path
	 */
	String storeTrailer(URL trailer, String basename);

	/**
	 * Re compose art path, check if file exists. If not, throw an IOException.
	 *
	 * @param relativePath
	 * @return
	 */
	Path getImagePath(String relativePath);

	/**
	 * Re compose trailer full path. If not, throw an IOException.
	 *
	 * @param relativePath
	 * @return
	 */
	Path getTrailerPath(String relativePath);
}
