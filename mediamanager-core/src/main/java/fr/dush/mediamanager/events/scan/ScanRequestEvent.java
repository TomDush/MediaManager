package fr.dush.mediamanager.events.scan;

import lombok.Getter;
import lombok.Setter;

import com.google.common.io.Files;

import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.tree.RootDirectory;
import fr.dush.mediamanager.events.AbstractEvent;

/**
 * Base for events on {@link RootDirectory}.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class ScanRequestEvent extends AbstractEvent {

	/** Existing or new RootDirectory */
	private RootDirectory rootDirectory;

	private String subPath;

	public ScanRequestEvent(Object source, RootDirectory rootDirectory) {
		super(source);
		this.rootDirectory = rootDirectory;
	}

	public ScanRequestEvent(Object source, MediaType mediaType, String... paths) {
		super(source);
		if (paths.length == 0) {
			throw new IllegalArgumentException("paths must not be empty.");
		}

		this.rootDirectory = new RootDirectory(Files.getNameWithoutExtension(paths[0]), mediaType, paths);

	}
}
