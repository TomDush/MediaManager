package fr.dush.mediacenters.modules.webui.rest.dto;

import fr.dush.mediamanager.domain.media.MediaSummary;
import lombok.Data;

/** Information on player currently playing */
@Data
public class PlayerInfo {

    private String id;

    private long position;
    private long length;

    private boolean paused;

    private MediaSummary media;
    private String name;

}
