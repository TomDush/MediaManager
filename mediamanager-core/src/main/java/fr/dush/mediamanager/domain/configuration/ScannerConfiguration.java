package fr.dush.mediamanager.domain.configuration;

import lombok.Data;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

@Data
public class ScannerConfiguration {

    private Set<String> videoExtensions = newHashSet();

    private List<String> cleanStrings = newArrayList();

    private String dateRegex;

    private List<String> moviesStacking = newArrayList();

}
