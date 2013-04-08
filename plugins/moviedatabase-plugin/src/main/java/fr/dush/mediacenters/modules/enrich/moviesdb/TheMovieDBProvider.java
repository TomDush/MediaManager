package fr.dush.mediacenters.modules.enrich.moviesdb;

import javax.enterprise.inject.Produces;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

public class TheMovieDBProvider {

	@Produces
	public TheMovieDbApi provideTheMovieDbApi() throws MovieDbException {

		return new TheMovieDbApi("21fa5e6aa76429cedfa1d628ecc7abeb"); // TODO get valid code, and protected it...
	}
}
