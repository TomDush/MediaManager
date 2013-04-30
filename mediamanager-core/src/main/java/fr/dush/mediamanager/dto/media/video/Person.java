package fr.dush.mediamanager.dto.media.video;

import java.io.Serializable;

import lombok.Data;
import fr.dush.mediamanager.dto.media.Sources;

/**
 * Person which can be actor, producer, ...
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
public class Person implements Serializable {

	private Sources sourceIds = new Sources();

	private String name;

	private String picture;

}
