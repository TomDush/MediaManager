package fr.dush.mediamanager.events.scan.reponses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
import fr.dush.mediamanager.dto.tree.RootDirectory;

/**
 * Event fired when scan starting.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InprogressScanningResponseEvent extends ScanningResponseEvent {

	private ScanningStatus status;

	public InprogressScanningResponseEvent(Object source, Object eventSource, RootDirectory rootDirectory, ScanningStatus status) {
		super(source, eventSource, rootDirectory);
		this.status = status;
	}

}
