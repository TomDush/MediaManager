package fr.dush.mediacenters.modules.webui.controllers;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.dush.mediamanager.dao.media.IMovieDAO;

@ApplicationScoped
@Named("providerData")
public class ProviderData {

	private static int instanceCount = 0;

	private int instance;

	@Inject
	private IMovieDAO movieDAO;

	@PostConstruct
	public void initInstance() {
		instance = instanceCount++;
	}

	public String getHello() {
		return String.format("There are %d movies in database.", movieDAO.findAll().size());
	}

	public Date getDate() {
		return new Date();
	}

	@Override
	public String toString() {
		return "ProviderData [instance=" + instance + "]";
	}
}
