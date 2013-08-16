package fr.dush.mediacenters.modules.enrich.moviesdb;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import lombok.Setter;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

import fr.dush.mediacenters.modules.enrich.TheMovieDbEnricher;
import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

public class TheMovieDBProvider {

	@Inject
	@Configuration(entryPoint = TheMovieDbEnricher.class)
	@Setter
	private ModuleConfiguration configuration;

	@Produces
	public TheMovieDbApi provideTheMovieDbApi() throws MovieDbException {
		// TODO MoviesDB : how to protect this private key ?
		return new TheMovieDbApi(configuration.readValue("moviesdb.key", "21fa5e6aa76429cedfa1d628ecc7abeb"));
	}
}
