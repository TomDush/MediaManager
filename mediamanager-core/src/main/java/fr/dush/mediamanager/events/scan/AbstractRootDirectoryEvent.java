package fr.dush.mediamanager.events.scan;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.tree.RootDirectory;

/**
 * Base for events on {@link RootDirectory}.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractRootDirectoryEvent implements Serializable {

	private Object source;

	private RootDirectory rootDirectory;
}
