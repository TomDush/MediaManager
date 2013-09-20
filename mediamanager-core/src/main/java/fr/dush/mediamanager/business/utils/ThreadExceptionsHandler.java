package fr.dush.mediamanager.business.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.annotations.Startup;
import fr.dush.mediamanager.events.lifecycle.ExceptionEvent;

/**
 * Handle exceptions which is not handled by threads.
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
@Startup
public class ThreadExceptionsHandler implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExceptionsHandler.class);

	@Inject
	@Getter
	private Event<ExceptionEvent> exceptionEventBus;

	@PostConstruct
	public void registerItSelf() {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.error("Thread [{}] throw exception : {}", t.getName(), e.getMessage(), e);
		exceptionEventBus.fire(new ExceptionEvent(t, "Thread " + t.getName() + " failed : " + e.getMessage(), e));
	}

}
