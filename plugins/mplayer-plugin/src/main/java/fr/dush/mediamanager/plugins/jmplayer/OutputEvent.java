package fr.dush.mediamanager.plugins.jmplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Thomas Duchatelle
 */
@AllArgsConstructor
@Getter
public class OutputEvent {

    private Level level;
    private String line;

}
