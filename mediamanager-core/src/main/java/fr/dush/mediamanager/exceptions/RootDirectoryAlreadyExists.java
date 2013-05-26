package fr.dush.mediamanager.exceptions;

import static com.google.common.collect.Sets.*;

import java.nio.file.Path;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.dush.mediamanager.dto.tree.RootDirectory;

@Data
@EqualsAndHashCode(callSuper = true, of = {})
@SuppressWarnings("serial")
public class RootDirectoryAlreadyExists extends Exception {

	/** Root directory containing an error */
	private RootDirectory rootDirectory;

	/** Invalid paths */
	private Set<Path> pathConcerned = newHashSet();

}
