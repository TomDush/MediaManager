package fr.dush.mediamanager.domain.tree;

import static com.google.common.collect.Sets.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class RootDirectory implements Serializable {

	/** Root name (to be displayed) */
	@Id
	private String name;

	/** Expected media type found */
	private MediaType mediaType;

	/** Module to use to enrich medias */
	private String enricher;

	/** Local directories (Using String because Morphia can't convert Path :( ) */
	private Set<String> paths = newHashSet();

	/** Last scanning date */
	private Date lastRefresh = null;

	public RootDirectory(String name, MediaType mediaType, String... paths) {
		this.name = name;
		this.mediaType = mediaType;
		this.paths = newHashSet(paths);
	}

	public RootDirectory(String name, MediaType mediaType, Set<String> paths) {
		this.name = name;
		this.mediaType = mediaType;
		this.paths = paths;
	}
}
