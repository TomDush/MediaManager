package fr.dush.mediamanager.dto.media.video;

import java.io.Serializable;

import lombok.Data;

/**
 * Person which can be actor, producer, ...
 *
 * @author Thomas Duchatelle
 */
@SuppressWarnings("serial")
@Data
public class Person implements Serializable {

	private String firstname;

	private String lastname;

}
