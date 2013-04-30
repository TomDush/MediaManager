package fr.dush.mediamanager.modulesapi.enrich;

import static com.google.common.collect.Collections2.*;
import static com.google.common.collect.Sets.*;

import java.util.regex.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.common.base.Function;

/**
 * File name, after it has been parsed.
 *
 * @author Thomas Duchatelle
 *
 */
@Data
@NoArgsConstructor
public class ParsedFileName {
	private Pattern yearPattern = Pattern.compile("(.*)([0-9]{4}).*");

	/** Movie name (after useless data removed from filename) */
	private String movieName;

	/** If it's defined in file name, movie release date */
	private int year = 0;

	/** If movies is splitted between multiple files */
	private String volume;

	/** Ignored filename's part */
	private String ignored;

	/** File extension */
	private String extension;

	public ParsedFileName(String movieName, int year) {
		super();
		this.movieName = movieName;
		this.year = year;
	}

	/**
	 * Compare extension with some given (case insensitive)
	 *
	 * @param extentions Extensions to match
	 * @return true if extension match with one.
	 */
	public boolean compare(String... extentions) {
		return transform(newHashSet(extentions), new Function<String, String>() {

			@Override
			public String apply(String input) {
				return null == input ? "" : input.toLowerCase();
			}
		}).contains(extension.toLowerCase());
	}
}
