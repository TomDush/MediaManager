package fr.dush.mediamanager.events.scan;

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
public class InprogressScanning extends AbstractRootDirectoryEvent {

	private ScanningStatus status;

	public InprogressScanning(Object source, RootDirectory rootDirectory, ScanningStatus status) {
		super(source, rootDirectory);
		this.status = status;
	}

}
