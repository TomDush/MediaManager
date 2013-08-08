package fr.dush.mediamanager.dto.tree;

import static com.google.common.collect.Sets.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * Media root directory.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Entity(noClassnameStored = true)
@Data
@NoArgsConstructor
public class RootDirectory implements Serializable {

	/** Root name (to be displayed) */
	@Id
	private String name;

	/** Local directories (Using String because Morphia can't convert Path :( ) */
	private Set<String> paths = newHashSet();

	/** Last scanning date */
	private Date lastRefresh;

	/** Module to use to enrich medias */
	private String enricherScanner;


	public RootDirectory(String name, String enricherScanner, String... paths) {
		this.name = name;
		this.enricherScanner = enricherScanner;
		this.paths = newHashSet(paths);
	}

	public RootDirectory(String name, String enricherScanner, Set<String> paths) {
		this.name = name;
		this.enricherScanner = enricherScanner;
		this.paths = paths;
	}

}
