package fr.dush.mediamanager.domain.media.video;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;
import lombok.NoArgsConstructor;
import fr.dush.mediamanager.domain.media.SourceId;
import fr.dush.mediamanager.domain.media.Sources;

/**
 * Person which can be actor, producer, ...
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
public class Person implements Serializable {

	private Sources sourceIds = new Sources();

	private String name;

	private String picture;

	public Person(String name, SourceId... sourceIds) {
		this.name = name;
		this.sourceIds.addAll(Arrays.asList(sourceIds));
	}

}
