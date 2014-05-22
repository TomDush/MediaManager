package fr.dush.mediamanager.tools;

import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Provide configuration and utilities for MongoDB.
 * 
 * @author Thomas Duchatelle
 */
@org.springframework.context.annotation.Configuration
public class MongoProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

    @Value("${mongodb.host}")
    private String host;
    @Value("${mongodb.port}")
    private Integer port;
    @Value("${mongodb.databaseName}")
    private String dbname;

    private MongoClient mongoClient;

    private DB db = null;

    private Jongo jongo;

    @PreDestroy
    public void closeConnection() {
        LOGGER.debug("Close MongoDB connection.");
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Bean
    public MongoClient producesMogoClient() throws UnknownHostException {
        LOGGER.debug("Instanciate new MongoClient");
        if (mongoClient == null) {
            mongoClient = new MongoClient(host, port);
        }

        return mongoClient;
    }

    @Bean
    public DB producesMogoDB() throws UnknownHostException {
        if (db == null) {
            LOGGER.debug("Instanciate new MongoDB and connection");
            db = producesMogoClient().getDB(dbname);
        }

        return db;
    }

    /** Jongo driver to MongoDB natural and fast use */
    @Bean
    public Jongo producesJongoConfig(DB mongodb) {
        if (jongo == null) {
            jongo = new Jongo(mongodb);
        }
        return jongo;
    }
}
