package fr.dush.mediamanager.exceptions;

import static com.google.common.collect.Lists.*;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.tree.RootDirectory;

@Data
@EqualsAndHashCode(callSuper = true, of = {})
@SuppressWarnings("serial")
@NoArgsConstructor
public class RootDirectoryAlreadyExistsException extends Exception {

	/** Root directory containing an error */
	private RootDirectory rootDirectory;

	/** Invalid paths */
	private List<String> pathConcerned = newArrayList();

	public RootDirectoryAlreadyExistsException(String message, RootDirectory rootDirectory, List<String> pathConcerned) {
		super(message);
		this.rootDirectory = rootDirectory;
		this.pathConcerned = pathConcerned;
	}

}
