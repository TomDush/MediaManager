package fr.dush.mediamanager.dao.mongodb;

import java.net.UnknownHostException;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Provide configuration and utilities for MongoDB.<br/>
 * TODO Injecter le paramétrage !
 *
 * @author Thomas Duchatelle
 */
public class MongoProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

	/**
	 * MongoDB server
	 */
	private String host = "localhost";

	/**
	 * Port to connect to MongoDB
	 */
	public static int port = 12345;

	/**
	 * Database's name
	 */
	private String databaseName = "junit";

	@Produces
	@Singleton
	public MongoClient provideMogoClient() throws UnknownHostException {
		LOGGER.debug("Instanciate new MongoDB and connection");
		return new MongoClient(host, port);
	}

	@Produces
	@Singleton
	public DB provideMogoDB(MongoClient mongoClient) throws UnknownHostException {
		LOGGER.debug("Instanciate new MongoDB and connection");
		return mongoClient.getDB(databaseName);
	}

}
