package fr.dush.mediamanager.events.scan.reponses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.tree.RootDirectory;

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
public class ScanningErrorResponseEvent extends ScanningResponseEvent {

	private String message;

	/**
	 * Exception raised : {@link fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException} or
	 * {@link fr.dush.mediamanager.exceptions.ScanningException}...
	 */
	private Exception exception;

	public ScanningErrorResponseEvent(Object source, Object eventSource, RootDirectory rootDirectory, Exception exception) {
		super(source, eventSource, rootDirectory);
		this.exception = exception;
		this.message = exception.getMessage();
	}

	public ScanningErrorResponseEvent(Object source, Object eventSource, RootDirectory rootDirectory, String message) {
		super(source, eventSource, rootDirectory);
		this.message = message;
	}

}
