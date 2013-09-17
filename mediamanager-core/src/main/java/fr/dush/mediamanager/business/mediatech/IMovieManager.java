package fr.dush.mediamanager.business.mediatech;

import java.util.List;

import fr.dush.mediamanager.dto.media.video.Movie;

public interface IMovieManager {

	List<Movie> findLastMovies(int number);

	List<Movie> findAllMovies();

}
