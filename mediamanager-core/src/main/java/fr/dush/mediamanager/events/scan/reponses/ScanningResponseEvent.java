package fr.dush.mediamanager.events.scan.reponses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.events.scan.request.AbstractRootDirectoryEvent;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ScanningResponseEvent extends AbstractRootDirectoryEvent {

	private Object eventSource;

	public ScanningResponseEvent(Object source, Object eventSource, RootDirectory rootDirectory) {
		super(source, rootDirectory);
		this.eventSource = eventSource;
	}

}
