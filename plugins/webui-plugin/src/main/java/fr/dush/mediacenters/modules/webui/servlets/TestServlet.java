package fr.dush.mediacenters.modules.webui.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.dush.mediacenters.modules.webui.controllers.ProviderData;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	@Inject
	private ProviderData providerData;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().append("Provider data : " + providerData);
	}

}
