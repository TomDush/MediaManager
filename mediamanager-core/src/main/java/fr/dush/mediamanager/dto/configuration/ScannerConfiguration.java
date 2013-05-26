package fr.dush.mediamanager.dto.configuration;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class ScannerConfiguration {

	private Set<String> videoExtensions = newHashSet();

	private List<String> cleanStrings = newArrayList();

	private String dateRegex;

	private List<String> moviesStacking = newArrayList();

}
