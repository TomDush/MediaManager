package fr.dush.mediamanager.business.mediatech.impl;

import static com.google.common.collect.Lists.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.dush.mediamanager.business.mediatech.IMovieManager;
import fr.dush.mediamanager.dao.media.IMovieDAO;
import fr.dush.mediamanager.dto.media.video.Movie;

@ApplicationScoped
@Named("movieManager")
public class MovieManagerImpl implements IMovieManager {

	@Inject
	private IMovieDAO movieDAO;

	@Override
	public List<Movie> findLastMovies(int number) {
		// TODO Not implemented method...
		return newArrayList();
	}

	@Override
	public List<Movie> findAllMovies() {
		return movieDAO.findAll();
	}

}
