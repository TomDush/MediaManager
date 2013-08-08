package fr.dush.mediamanager.engine.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.IStreamProcessor;

/**
 * Logger for embedded mongo db.
 *
 * @author Thomas Duchatelle
 *
 */
public class MongoDBLogger extends ProcessOutput {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBLogger.class);

	/**
	 * Instantiate logger for embedded mongo db.
	 */
	public MongoDBLogger() {
		super(new IStreamProcessor() {

			@Override
			public void process(String block) {
				LOGGER.debug(block);
			}

			@Override
			public void onProcessed() {
			}
		}, new IStreamProcessor() {

			@Override
			public void process(String block) {
				LOGGER.error(block);
			}

			@Override
			public void onProcessed() {
			}
		}, new IStreamProcessor() {

			@Override
			public void process(String block) {
				LOGGER.info(block);
			}

			@Override
			public void onProcessed() {
			}
		});
	}
}
