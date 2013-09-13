package fr.dush.mediacenters.modules.webui;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import fr.dush.mediamanager.dao.media.IMovieDAO;

@Named("providerData")
public class ProviderData {

	@Inject
	private IMovieDAO movieDAO;

	public String getHello() {
		return String.format("There are %d movies in database.", movieDAO.findAll().size());
	}

	public Date getDate() {
		return new Date();
	}
}
