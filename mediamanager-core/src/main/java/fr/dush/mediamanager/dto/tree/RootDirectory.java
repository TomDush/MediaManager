package fr.dush.mediamanager.dto.tree;

import java.nio.file.*;
import java.util.*;

import lombok.*;

/**
 * Media root directory.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
public class RootDirectory {

	private String name;

	private Path path;

	private Date lastRefresh;

}
