package fr.dush.mediamanager.dao.mongodb;

import java.net.UnknownHostException;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
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
import fr.dush.mediamanager.annotations.ConfigurationWithoutDatabase;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.dao.mongodb.converters.PathConverter;

/**
 * Provide configuration and utilities for MongoDB.
 *
 * @author Thomas Duchatelle
 */
@Module(id = "mongo-provider", name = "MongoDB Configurator", packageName = "persistence")
@ApplicationScoped
public class MongoProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

	@Inject
	@ConfigurationWithoutDatabase
	@Configuration(definition = "configuration/mongo-config.json")
	private ModuleConfiguration configuration;

	private MongoClient mongoClient;

	private DB db = null;

	private Datastore datastore = null;

	@PreDestroy
	public void closeConnection() {
		LOGGER.debug("Close MongoDB connection.");
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Produces
	@ApplicationScoped
	public MongoClient producesMogoClient() throws UnknownHostException {
		LOGGER.debug("Instanciate new MongoClient");
		mongoClient = new MongoClient(configuration.readValue("mongodb.host"), configuration.readValueAsInt("mongodb.port"));
		return mongoClient;
	}

	@Produces
	public DB producesMogoDB(MongoClient mongoClient) throws UnknownHostException {
		if (db == null) {
			LOGGER.debug("Instanciate new MongoDB and connection");
			db = mongoClient.getDB(configuration.readValue("mongodb.databaseName"));
		}

		return db;
	}

	@Produces
	public Datastore producesMorphiaConfig(DB mongodb) {
		if (datastore == null) {
			Morphia morphia = new Morphia();
			morphia.mapPackage("fr.dush.mediamanager.domain");
			morphia.getMapper().getConverters().addConverter(PathConverter.class);

			datastore = morphia.createDatastore(mongodb.getMongo(), configuration.readValue("mongodb.databaseName"));
		}

		return datastore;
	}

}
