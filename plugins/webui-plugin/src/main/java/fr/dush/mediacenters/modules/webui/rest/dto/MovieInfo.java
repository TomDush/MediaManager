package fr.dush.mediacenters.modules.webui.rest.dto;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import fr.dush.mediamanager.domain.media.video.Person;
import lombok.Data;

/** Light movie data */
@Data
public class MovieInfo {

    public String id;
    public String title;
    private String poster;

    private double voteAverage;
    public String release;
    private String tagline;

    private Set<String> genres = newHashSet();
    private List<Person> mainActors = newArrayList();

}
