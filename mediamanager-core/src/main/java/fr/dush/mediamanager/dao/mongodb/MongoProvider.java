package fr.dush.mediamanager.dao.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.annotations.ConfigurationWithoutDatabase;
import fr.dush.mediamanager.annotations.Module;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * Provide configuration and utilities for MongoDB.
 *
 * @author Thomas Duchatelle
 */
@Module(id = "mongo-provider", name = "MongoDB Configurator", packageName = "persistence")
@org.springframework.context.annotation.Configuration
public class MongoProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);

    @Inject
    @ConfigurationWithoutDatabase
    @Configuration(definition = "configuration/mongo-config.json")
    private ModuleConfiguration configuration;

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
            mongoClient = new MongoClient(configuration.readValue("mongodb.host"),
                                          configuration.readValueAsInt("mongodb.port"));
        }

        return mongoClient;
    }

    @Bean
    public DB producesMogoDB() throws UnknownHostException {
        if (db == null) {
            LOGGER.debug("Instanciate new MongoDB and connection");
            db = producesMogoClient().getDB(configuration.readValue("mongodb.databaseName"));
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
