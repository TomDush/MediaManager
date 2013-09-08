package fr.dush.mediamanager.exceptions;

import static com.google.common.collect.Lists.*;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.google.common.base.Joiner;

import fr.dush.mediamanager.dto.tree.RootDirectory;

@Getter
@Setter
@SuppressWarnings("serial")
@NoArgsConstructor
public class RootDirectoryAlreadyExistsException extends Exception {

	/** Root directory containing an error */
	private RootDirectory rootDirectory;

	/** Invalid paths */
	private List<String> pathConcerned = newArrayList();

	public RootDirectoryAlreadyExistsException(String message, RootDirectory rootDirectory, List<String> pathConcerned) {
		super(generateMessage(message, rootDirectory, pathConcerned));

		this.rootDirectory = rootDirectory;
		this.pathConcerned = pathConcerned;
	}

	private static String generateMessage(String message, RootDirectory rootDirectory, List<String> pathConcerned) {
		StringBuilder sb = new StringBuilder();
		sb.append("Couldn't save RootDirectory '").append(rootDirectory.getName()).append("' : ");
		sb.append(message);
		if (!message.endsWith(".")) {
			sb.append(".");
		}

		if (pathConcerned != null && !pathConcerned.isEmpty()) {
			sb.append("Conflicts paths : ").append(Joiner.on(", ").join(pathConcerned)).append(".");
		}

		return sb.toString();
	}

}
