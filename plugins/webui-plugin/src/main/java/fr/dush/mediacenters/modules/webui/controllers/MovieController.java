package fr.dush.mediacenters.modules.webui.controllers;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import fr.dush.mediamanager.business.mediatech.IMovieManager;
import fr.dush.mediamanager.dto.media.video.Movie;

@Named("movieController")
public class MovieController {

	@Inject
	private IMovieManager movieManager;

	public String getMoviesSize() {
		return String.format("There are %d movies in database.", movieManager.findAllMovies().size());
	}

	public List<Movie> getAllMovies() {
		return movieManager.findAllMovies();
	}

	public Date getDate() {
		return new Date();
	}
}
