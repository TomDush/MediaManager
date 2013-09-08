package fr.dush.mediamanager.events.scan;

import lombok.Getter;
import lombok.Setter;
import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.events.AbstractEvent;

@SuppressWarnings("serial")
@Getter
@Setter
public class ScanResponseEvent extends AbstractEvent {

	private ScanRequestEvent eventSource;

	/** In progress, success, or failed... */
	private ScanStatus scanStatus;

	public ScanResponseEvent(Object source, ScanRequestEvent eventSource, ScanStatus scanStatus) {
		super(source);
		this.eventSource = eventSource;
		this.scanStatus = scanStatus;
	}

	public ScanResponseEvent(Object source, ScanRequestEvent eventSource, String failedMessage) {
		this(source, eventSource, new ScanStatus(failedMessage));
	}

	public boolean hasFailed() {
		return scanStatus == null || scanStatus.hasFailed();
	}
}
