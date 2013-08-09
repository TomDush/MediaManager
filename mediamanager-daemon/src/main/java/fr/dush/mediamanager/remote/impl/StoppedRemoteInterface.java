package fr.dush.mediamanager.remote.impl;

import static com.google.common.collect.Lists.*;

import java.rmi.RemoteException;
import java.util.List;

import javax.enterprise.inject.Alternative;

import fr.dush.mediamanager.business.mediatech.scanner.ScanningStatus;
import fr.dush.mediamanager.launcher.Status;
import fr.dush.mediamanager.remote.ConfigurationField;
import fr.dush.mediamanager.remote.MediaManagerRMI;

@Alternative
public class StoppedRemoteInterface implements MediaManagerRMI {
	@Override
	public void stop() {
	}

	@Override
	public void scan(String scanerName, String absolutePath) {
	}

	@Override
	public Status getStatus() {
		return Status.STOPPED;
	}

	@Override
	public List<ScanningStatus> getInprogressScanning() {
		return newArrayList();
	}

	@Override
	public List<ConfigurationField> getFullConfiguration() throws RemoteException {
		return newArrayList();
	}

}