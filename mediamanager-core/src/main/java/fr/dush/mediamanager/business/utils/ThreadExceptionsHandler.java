package fr.dush.mediamanager.business.utils;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.events.lifecycle.ExceptionEvent;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Handle exceptions which is not handled by threads.
 *
 * @author Thomas Duchatelle
 */
@Named
public class ThreadExceptionsHandler implements UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExceptionsHandler.class);

    @Inject
    @Getter
    private EventBus eventBus;

    @PostConstruct
    public void registerItSelf() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Thread [{}] throw exception : {}", t.getName(), e.getMessage(), e);
        eventBus.post(new ExceptionEvent(t, "Thread " + t.getName() + " failed : " + e.getMessage(), e));
    }

}
