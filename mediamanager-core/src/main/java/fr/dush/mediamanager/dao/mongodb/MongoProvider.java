package fr.dush.mediamanager.dao.mongodb;

import java.net.UnknownHostException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

/**
 * Provide configuration and utilities for MongoDB.
 *
 * @author Thomas Duchatelle
 */
@Module(id = "mongo-provider", name="MongoDB Configurator")
public class MongoProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

	@Inject
	@Configuration(definition = "configuration/mongo-config.json")
	private ModuleConfiguration configuration;

	@Produces
//	@ApplicationScoped
	public MongoClient producesMogoClient() throws UnknownHostException {
		LOGGER.debug("Instanciate new MongoDB and connection");
		return new MongoClient(configuration.getValue("mongodb.host"), configuration.getValueAsInt("mongodb.port"));
	}

	@Produces
//	@ApplicationScoped
	public DB producesMogoDB(MongoClient mongoClient) throws UnknownHostException {
		LOGGER.debug("Instanciate new MongoDB and connection");
		return mongoClient.getDB(configuration.getValue("mongodb.databaseName"));
	}

	@Produces
//	@ApplicationScoped
	public Datastore producesMorphiaConfig(DB mongodb) {
		Morphia morphia = new Morphia();
		morphia.mapPackage("fr.dush.mediamanager.dto");

		return morphia.createDatastore(mongodb.getMongo(), configuration.getValue("mongodb.databaseName"));
	}

}
