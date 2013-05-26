package fr.dush.mediamanager.business.mediatech.scanner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Scanning status, thread safe and designed to be updated in real time.
 *
 * @author Thomas Duchatelle
 *
 */
@Getter
public class ScanningStatus {

	@Getter(value = AccessLevel.PROTECTED)
	private Thread scanningThread;

	/** Current phase */
	private Phase phase = Phase.INIT;

	/** Number of jobs */
	private int jobNumber = 0;

	/** Jobs finished */
	private int jobDone = 0;

	/** Step name */
	@Setter
	private String stepName;

	/** Progress in phase 0 to 100. Negative if progression isn't known. */
	public double getPercent() {
		return jobNumber <= 0 ? -1 : 100.0 * jobDone / jobNumber;
	}

	public ScanningStatus(Thread scanningThread) {
		this.scanningThread = scanningThread;
	}

	public synchronized void changePhase(Phase phase, int jobNumber) {
		this.phase = phase;
		this.jobNumber = jobNumber;

		stepName = "";
		jobDone = 0;
	}

	public synchronized void changePhase(Phase phase, int jobNumber, String stepName) {
		this.phase = phase;
		this.stepName = stepName;
		this.jobNumber = jobNumber;

		jobDone = 0;
	}

	public synchronized void incrementFinishedJob(int number) {
		jobDone += number;
	}

	public boolean isInProgress() {
		return scanningThread.isAlive();
	}

	@Override
	public String toString() {
		return phase + " " + stepName + " : " + getPercent() + "% (" + jobDone + "/" + jobNumber + ")";
	}

	public enum Phase {
		/** Intiale step ... */
		INIT,

		/** Scanning files, parse names */
		SCANNING,

		/** Get from internet missing data on medias. */
		ENRICH;

	}
}
