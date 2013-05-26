package fr.dush.mediamanager.dto.tree;

import static com.google.common.collect.Sets.*;

import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

import lombok.Data;

/**
 * Media root directory.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
public class RootDirectory {

	/** Root name (to be displayed) */
	private String name;

	/** Local directories */
	private Set<Path> paths = newHashSet();

	/** Last scanning date */
	private Date lastRefresh;

	/** Module to use to enrich medias */
	private String enricherScanner;

}
