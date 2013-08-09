package fr.dush.mediamanager.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
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
	 * @param scanerName File enricher's name
	 * @param absolutePath
	 */
	void scan(String scanerName, String absolutePath) throws RemoteException;

	/**
	 * Get Inprogress process...
	 *
	 * @return
	 * @throws RemoteException
	 */
	List<ScanningStatus> getInprogressScanning() throws RemoteException;

	/**
	 * Get configuration for each modules...
	 *
	 * @return
	 * @throws RemoteException
	 */
	List<ConfigurationField> getFullConfiguration() throws RemoteException;

}
