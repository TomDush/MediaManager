package fr.dush.mediamanager.engine.mongodb;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;
import fr.dush.mediamanager.annotations.Configuration;
import fr.dush.mediamanager.business.configuration.ModuleConfiguration;
import fr.dush.mediamanager.exceptions.InitializationException;

/**
 * Start embedded mongo db.
 *
 * @author Thomas Duchatelle
 */
@ApplicationScoped
public class EmbeddedMongoDbControler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedMongoDbControler.class);

	@Inject
	@Configuration(definition = "configuration/mongo-config.json")
	private ModuleConfiguration configuration;

	private MongodProcess embeddedMongo;

	public void startEmbeddedMogonDB() {
		LOGGER.debug("Create Embedded MongoDB");

		final int port = configuration.getValueAsInt("mongodb.port");

		try {
			// Configure mongo db
			RuntimeConfig runtimeConfig = new RuntimeConfig();
			runtimeConfig.setExecutableNaming(new UserTempNaming());
			runtimeConfig.setProcessOutput(new MongoDBLogger());

			MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

			MongodConfig mongodConfig = new MongodConfig(Version.Main.V2_3, port, Network.localhostIsIPv6());

			MongodExecutable mongodExecutable = runtime.prepare(mongodConfig);

			// Start engine
			embeddedMongo = mongodExecutable.start();
		} catch (IOException e) {
			throw new InitializationException("Error while initialize embedded mongo db : " + e.getMessage(), e);
		}
	}

	public void stopEmbeddedMogonDB() {
		embeddedMongo.stop();
	}
}
