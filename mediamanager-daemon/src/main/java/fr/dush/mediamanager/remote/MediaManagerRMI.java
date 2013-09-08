package fr.dush.mediamanager.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import fr.dush.mediamanager.dto.scan.ScanStatus;
import fr.dush.mediamanager.dto.tree.MediaType;
import fr.dush.mediamanager.launcher.Status;

/**
 * Interface implemented by the daemon to response to Remote Procedure Call
 *
 * @author Thomas Duchatelle
 *
 */
public interface MediaManagerRMI extends Remote {

	/**
	 * Stop media manager.
	 */
	void stop() throws RemoteException;

	/**
	 * Get application status.
	 *
	 * @return
	 */
	Status getStatus() throws RemoteException;

	/**
	 * Scan or refresh absolute path
	 *
	 * @param type Path content's type
	 * @param absolutePath
	 * @param enricher File enricher's name
	 */
	public void scan(MediaType type, String absolutePath, String enricher) throws RemoteException;

	/**
	 * Get Inprogress process...
	 *
	 * @return
	 * @throws RemoteException
	 */
	List<ScanStatus> getInprogressScanning() throws RemoteException;

	/**
	 * Get configuration for each modules...
	 *
	 * @return
	 * @throws RemoteException
	 */
	List<ConfigurationField> getFullConfiguration() throws RemoteException;

}
