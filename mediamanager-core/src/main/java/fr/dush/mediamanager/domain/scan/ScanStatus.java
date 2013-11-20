package fr.dush.mediamanager.domain.scan;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Scanning status, thread safe and designed to be updated in real time.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
public class ScanStatus implements Serializable {

	/** Current phase */
	private Phase phase = Phase.INIT;

	/** Number of jobs */
	private int jobNumber = 0;

	/** Jobs finished */
	private int jobDone = 0;

	/** Step name */
	@Setter
	private String stepName = "";

	/** Error message if any */
	@Setter
	private String message;

	/** Occured exception if any ... */
	@Setter
	private Throwable exception;

	/**
	 * Constructor for failed process
	 *
	 * @param message
	 */
	public ScanStatus(String message) {
		phase = Phase.FAILED;
		this.message = message;
	}

	public ScanStatus(String message, Throwable throwable) {
		this(message);
		this.exception = throwable;
	}

	/** Progress in phase 0 to 100. Negative if progression isn't known. */
	public double getPercent() {
		return jobNumber <= 0 ? -1 : 100.0 * jobDone / jobNumber;
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
		return phase != Phase.FAILED && phase != Phase.SUCCED;
	}

	public boolean hasFailed() {
		return phase == Phase.FAILED;
	}

	public boolean hasSucced() {
		return phase == Phase.SUCCED;
	}

	public void setException(String message, Throwable exception) {
		this.message = message;
		this.exception = exception;
	}

	public String getMessage() {
		if (exception != null && isEmpty(message)) {
			return exception.getMessage();
		}

		return message;
	}

	@Override
	public String toString() {
		if (phase == Phase.FAILED) {
			return "FAILED : " + message;
		}

		final double percent = getPercent();
		if (percent != -1) {
			return String.format("%s %s : %.2f%% (%d/%d)", phase, stepName, percent, jobDone, jobNumber);
		}

		return phase + " " + stepName + (jobDone > 0 ? " : " + jobDone : "");
	}
}
