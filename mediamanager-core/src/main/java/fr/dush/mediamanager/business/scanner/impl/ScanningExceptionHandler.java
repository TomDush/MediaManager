package fr.dush.mediamanager.business.scanner.impl;

import com.google.common.eventbus.EventBus;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.lifecycle.ExceptionEvent;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.Thread.UncaughtExceptionHandler;

public class ScanningExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanningExceptionHandler.class);

    @Setter
    private ScanStatus status;

    @Inject
    @Getter
    private EventBus eventBus;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Scanning failed : {}", status, e);
        status.setException(e);

        this.getEventBus().post(new ExceptionEvent(this, "Scanning paths failed : " + e.getMessage(), e));
    }

}
