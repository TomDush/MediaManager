package fr.dush.mediamanager.events.scan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.dto.tree.RootDirectory;

/**
 * Event on adding a new root directory.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class NewRootDirectoryEvent extends AbstractRootDirectoryEvent {

	public NewRootDirectoryEvent(Object source, RootDirectory rootDirectory) {
		super(source, rootDirectory);
	}

}
