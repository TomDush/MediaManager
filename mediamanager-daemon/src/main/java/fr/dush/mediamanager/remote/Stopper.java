package fr.dush.mediamanager.remote;

import javax.enterprise.context.ApplicationScoped;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class notified when received event to close application.
 *
 * <p>
 * Use internal singleton to resolve concurrency locks from CDI's proxy.
 * </p>
 *
 * @author Thomas Duchatelle
 *
 */
@ApplicationScoped
public class Stopper {

	private static final Logger LOGGER = LoggerFactory.getLogger(Stopper.class);

	private static StopperSynchronizer instance;

	private synchronized StopperSynchronizer getInstance() {
		if (instance == null) {
			instance = new StopperSynchronizer();
		}

		return instance;
	}

	public void stopApplication() {
		LOGGER.debug("--> stopApplication {}", this);

		getInstance().stopApplication();
	}

	public void waitApplicationEnd() throws InterruptedException {
		LOGGER.debug("--> waitApplicationEnd {}", this);

		getInstance().waitApplicationEnd();
	}

	@Getter
	@Setter
	private static class StopperSynchronizer {

		private boolean mustBeStopped = false;

		public synchronized void stopApplication() {
			mustBeStopped = true;
			notify();
		}

		public synchronized void waitApplicationEnd() throws InterruptedException {
			while (!mustBeStopped) {
				wait();
			}
		}
	}
}
