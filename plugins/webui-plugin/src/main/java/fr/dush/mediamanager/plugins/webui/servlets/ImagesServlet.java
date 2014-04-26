package fr.dush.mediamanager.plugins.webui.servlets;

import fr.dush.mediamanager.business.mediatech.IArtManager;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.*;

@SuppressWarnings("serial")
public class ImagesServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesServlet.class);

    public static final String SIZE_PARAM = "size";

    @Inject
    private IArtManager artDownloader;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String path = formatPath(req.getRequestURI());

        LOGGER.debug("Resolving image: {}", path);

        boolean found = false;

        if (path != null) {
            try {
                found = artDownloader.readImage(path,
                                                convertQuality(req.getParameter(SIZE_PARAM)),
                                                resp.getOutputStream());

            } catch (Exception e) {
                LOGGER.error("Couldn't find image with path: {}. Error is: {}", path, e.getMessage(), e);

            }
        }

        if (!found) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Remove servlet path. Returned path wont start by /.
     */
    private static String formatPath(String requestURI) {
        if (isEmpty(requestURI)) {
            return null;
        }

        return requestURI.substring(1);
    }

    private ArtQuality convertQuality(String quality) {
        if (isEmpty(quality)) {
            return ArtQuality.THUMBS;
        }
        try {
            return ArtQuality.valueOf(quality.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("ArtQuality [{}] isn't know.", quality);
            return ArtQuality.THUMBS;
        }
    }
}
