package fr.dush.mediamanager.plugins.enrich.moviesdb;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Properties;

import javax.inject.Inject;

import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.tools.WebBrowser;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.exceptions.ConfigurationException;
import fr.dush.mediamanager.plugins.enrich.TheMovieDbEnricher;
import fr.dush.mediamanager.tools.RetryApi;

@org.springframework.context.annotation.Configuration
public class TheMovieDBProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDBProvider.class);

    @Inject
    @Configuration(entryPoint = TheMovieDbEnricher.class)
    @Setter
    private ModuleConfiguration configuration;

    @Bean
    public TheMovieDbApi provideTheMovieDbApi() throws MovieDbException {
        LOGGER.debug("Create TheMovieDbApi instance with proxy args : {}.", configuration.resolveProperties(
                "http://${proxy.username}:${proxy.password}@${proxy.host}:${proxy.port}", new Properties()));

        String host = configuration.readDeepValue("proxy.host", null);
        if (isNotEmpty(host)) {
            WebBrowser.setProxyHost(host);
            WebBrowser.setProxyPort(configuration.readValueAsInt("proxy.port"));
            WebBrowser.setProxyUsername(configuration.readDeepValue("proxy.username", null));
            WebBrowser.setProxyPassword(configuration.readDeepValue("proxy.password", null));
        }

        try {
            // TODO MoviesDB : how to protect this private key ?
            return RetryApi.retry(new RetryApi.MethodWithReturn<TheMovieDbApi>() {

                @Override
                public TheMovieDbApi doIt() throws Exception {
                    return new TheMovieDBRetryDecorator(configuration.readValue("moviesdb.key",
                            "21fa5e6aa76429cedfa1d628ecc7abeb"));
                }
            });
        }
        catch (Exception e) {
            throw new ConfigurationException(
                    "Can't initialise TheMovieDbApi, check your internet connection and proxy parameters.", e);
        }
    }
}
