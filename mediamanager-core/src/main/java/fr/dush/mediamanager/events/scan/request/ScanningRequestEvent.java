package fr.dush.mediamanager.events.scan.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request scanning on directory : must create RootDirectory if necessary, and scan it.
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanningRequestEvent implements Serializable {

	private Object source;

	private String path;

	private String scannerName;
}
