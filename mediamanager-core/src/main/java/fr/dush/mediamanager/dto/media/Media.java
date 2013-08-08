package fr.dush.mediamanager.dto.media;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;

/**
 * Meta-data on media, or group of medias.
 *
 * <p>
 * Media are identified by at least one id : {@link SourceId}.
 * </p>
 *
 * @author Thomas Duchatelle
 *
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(of = "mediaIds")
public abstract class Media implements Serializable {

	/** Generated ID. DAO has business logic to use {@link #mediaIds} as complex ID. */
	@Id
	private ObjectId id;

	/** Media pretty name : prefer resolved than file name. */
	private String title;

	/** Media poster or image representing media. */
	private String poster;

	/** Media identifiers */
	private Sources mediaIds = new Sources();

	/** This media has been seen <code>nb</code> times */
	private int seen = 0;

	/** Adding date into mediatech */
	private Date creation;

	/** Full meta-data in JSON language */
	private String otherMetaData;
}
