package fr.dush.mediamanager.events.scan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;
import fr.dush.mediamanager.exceptions.ScanningException;

/**
 * Event fired when scan failed...
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ScanningErrorEvent extends AbstractRootDirectoryEvent {

	/** Exception raised : {@link RootDirectoryAlreadyExistsException} or {@link ScanningException}... */
	private Exception exception;

	public ScanningErrorEvent(Object source, RootDirectory rootDirectory, Exception exception) {
		super(source, rootDirectory);
		this.exception = exception;
	}


}
