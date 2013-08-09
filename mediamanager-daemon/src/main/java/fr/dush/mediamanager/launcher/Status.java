package fr.dush.mediamanager.launcher;

import java.io.Serializable;

/**
 * Application status.
 *
 * @author Thomas Duchatelle
 *
 */
public enum Status implements Serializable {

	/** Application is started */
	STARTED,

	/** Application is starting ... */
	STARTING,

	/** Stopped : application doesn't respond */
	STOPPED;

}
