package fr.dush.mediamanager.business.scanner.impl;

import fr.dush.mediamanager.business.mediatech.IArtManager;
import fr.dush.mediamanager.domain.media.art.ArtQuality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Download arts in another thread...
 */
public class DownloaderThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloaderThread.class);
    public static final int CAPACITY = 100;

    @Inject
    private IArtManager artManager;

    private LinkedBlockingQueue<Request> queue = new LinkedBlockingQueue<>(CAPACITY);

    private boolean finished = false;

    public DownloaderThread() {
        setDaemon(true);
    }

    public void append(String artRef, ArtQuality... qualities) {
        if (finished) {
            throw new IllegalStateException(
                    "Can't append new request when DownloaderThread has been marked as 'finished'.");
        }

        try {
            queue.put(new Request(artRef, qualities));
        } catch (InterruptedException e) {
            LOGGER.warn("Don't append new request because thread has been interrupted.");
        }
    }

    public synchronized void markAsFinished() {
        finished = true;
    }

    @Override
    public void run() {
        LOGGER.warn("Starting to download arts...");
        try {
            while (!finished) {
                Request request = null;
                while (!finished && (request = queue.poll(200, TimeUnit.MILLISECONDS)) != null) {
                    try {
                        artManager.downloadArt(request.getArtRef(), request.getQualities());
                    } catch (Exception e) {
                        LOGGER.warn("Could not download {}", request, e);
                    }
                }

                LOGGER.debug("Finished to process request {}, finished={}", request, finished);

            }
        } catch (InterruptedException e) {
            LOGGER.warn("Download interrupted...");
        }

    }

    @Getter
    @AllArgsConstructor
    @ToString
    private class Request {

        private String artRef;
        private ArtQuality[] qualities;

    }
}
