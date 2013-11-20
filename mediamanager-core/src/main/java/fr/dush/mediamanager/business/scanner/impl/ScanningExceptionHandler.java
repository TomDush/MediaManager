package fr.dush.mediamanager.business.scanner.impl;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.lifecycle.ExceptionEvent;

public class ScanningExceptionHandler implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScanningExceptionHandler.class);

	@Setter
	private ScanStatus status;

	@Inject
	@Getter
	private Event<ExceptionEvent> exceptionEventBus;

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.error("Scanning failed : {}", status, e);
		status.setException(e);

		getExceptionEventBus().fire(new ExceptionEvent(this, "Scanning paths failed : " + e.getMessage(), e));
	}

}
