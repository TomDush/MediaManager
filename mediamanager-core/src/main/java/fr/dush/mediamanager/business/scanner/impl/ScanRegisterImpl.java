package fr.dush.mediamanager.business.scanner.impl;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Lists.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fr.dush.mediamanager.business.scanner.IScanRegister;
import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;
import fr.dush.mediamanager.events.scan.ScanResponseEvent;

/**
 * In-memory implementation if {@link IScanRegister} ; listen events.
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
public class ScanRegisterImpl implements IScanRegister {

	/** Max wait timeout */
	private static final int TIMEOUT = 3000;

	private List<ScanStatus> scans = newArrayList();

	/** Event.toString => scanning status */
	private Cache<ScanRequestEvent, ScanStatus> events = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
			.build();

	@Override
	public List<ScanStatus> getInprogressScans() {
		return newArrayList(filter(scans, new Predicate<ScanStatus>() {
			@Override
			public boolean apply(ScanStatus status) {
				return status.isInProgress();
			}
		}));
	}

	@Override
	public List<ScanStatus> getAllScans() {
		return Collections.unmodifiableList(scans);
	}

	@Override
	public ScanStatus waitResponseFor(ScanRequestEvent event) {
		try {
			if (!events.asMap().containsKey(event)) {
				synchronized (events) {
					if (!events.asMap().containsKey(event)) {
						events.wait(TIMEOUT);
					}
				}
			}
		} catch (InterruptedException e) {
			// Do nothing, continue...
		}

		return events.getIfPresent(event);
	}

	public void add(@Observes ScanResponseEvent event) {
		scans.add(event.getScanStatus());

		synchronized (events) {
			events.put(event.getEventSource(), event.getScanStatus());
			events.notifyAll();
		}
	}

}
