package fr.dush.mediamanager.plugins.webui.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** If media has been interrupted, this contains where it has been stopped. */
@Data
public class RecoveryDTO implements Serializable {

    /** Last known position (second) */
    private long position;

    /** Full length (second) */
    private long length;

    /** Media file in reading */
    private List<String> medias;
}
