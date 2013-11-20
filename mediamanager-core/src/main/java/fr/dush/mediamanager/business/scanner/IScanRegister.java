package fr.dush.mediamanager.business.scanner;

import java.util.List;

import fr.dush.mediamanager.domain.scan.ScanStatus;
import fr.dush.mediamanager.events.scan.ScanRequestEvent;

/**
 * Business layer to access to in progress scan or fished/failed scans.
 *
 * @author Thomas Duchatelle
 *
 */
public interface IScanRegister {

	List<ScanStatus> getInprogressScans();

	List<ScanStatus> getAllScans();

	ScanStatus waitResponseFor(ScanRequestEvent event);
}
