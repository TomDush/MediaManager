package fr.dush.mediacenters.modules.webui.servlets;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import fr.dush.mediamanager.business.mediatech.IArtDownloader;

@SuppressWarnings("serial")
public class ImagesServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImagesServlet.class);

	@Inject
	private IArtDownloader artDownloader;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String path = req.getRequestURI();

		LOGGER.debug("Resolving {} with {}", path, artDownloader);

		if (isNotEmpty(path) && path.startsWith("/")) {
			final Path imagePath = artDownloader.getImagePath(path.substring(1));

			LOGGER.debug("Provide {} art, if exists ({})", imagePath, imagePath.toFile().exists());

			if (imagePath.toFile().exists()) {
				resp.setContentType("image/" + Files.getFileExtension(imagePath.toString()));
				Files.copy(imagePath.toFile(), resp.getOutputStream());

			} else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

	}

}
