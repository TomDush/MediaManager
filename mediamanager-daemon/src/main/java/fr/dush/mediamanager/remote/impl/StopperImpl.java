package fr.dush.mediamanager.remote.impl;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.events.lifecycle.ApplicationStarted;
import fr.dush.mediamanager.remote.IStopper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Class notified when received event to close application. <p> Use internal singleton to resolve concurrency locks from
 * CDI's proxy. </p>
 *
 * @author Thomas Duchatelle
 */
@Service
public class StopperImpl implements IStopper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopperImpl.class);

    private static StopperSynchronizer instance;

    @Inject
    private EventBus eventBus;

    private static synchronized StopperSynchronizer getInstance() {
        if (instance == null) {
            instance = new StopperSynchronizer();
        }

        return instance;
    }

    @Override
    public void stopApplication() {
        LOGGER.debug("--> stopApplication {}", this);

        getInstance().stopApplication();
    }

    @Override
    public void waitApplicationEnd() throws InterruptedException {
        LOGGER.debug("--> waitApplicationEnd {}", this);

        getInstance().waitApplicationEnd();
    }

    @Override
    public void fireApplicationStarted(Object source) {
        eventBus.post(new ApplicationStarted(source));
    }

    @Getter
    @Setter
    private static class StopperSynchronizer {

        private boolean mustBeStopped = false;

        public synchronized void stopApplication() {
            mustBeStopped = true;
            notify();
        }

        public synchronized void waitApplicationEnd() throws InterruptedException {
            while (!mustBeStopped) {
                wait();
            }
        }
    }
}
