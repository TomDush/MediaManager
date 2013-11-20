package fr.dush.mediamanager.domain.configuration;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Alternative;

import lombok.Data;

@Data
@Alternative
public class ScannerConfiguration {

	private Set<String> videoExtensions = newHashSet();

	private List<String> cleanStrings = newArrayList();

	private String dateRegex;

	private List<String> moviesStacking = newArrayList();

}
