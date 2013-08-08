package fr.dush.mediamanager.dao.mongodb;

import java.net.UnknownHostException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dao.mongodb.converters.PathConverter;

/**
 * Provide configuration and utilities for MongoDB.
 *
 * @author Thomas Duchatelle
 */
@Module(id = "mongo-provider", name="MongoDB Configurator")
public class MongoProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

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
		morphia.getMapper().getConverters().addConverter(PathConverter.class);

		return morphia.createDatastore(mongodb.getMongo(), configuration.getValue("mongodb.databaseName"));
	}

}
