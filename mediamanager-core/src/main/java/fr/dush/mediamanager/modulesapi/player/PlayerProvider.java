package fr.dush.mediamanager.modulesapi.player;

import java.util.List;

/**
 * @author Thomas Duchatelle
 */
public interface PlayerProvider {

    Player createPlayerInstance();

    List<String> managedExtensions();

}
