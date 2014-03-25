package fr.dush.mediamanager.events.play;

import fr.dush.mediamanager.modulesapi.player.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Event to control a player */
@Data
@AllArgsConstructor
public abstract class PlayerControlEvent {

    /** Type of request */
    private PlayerControl request;

    /** (optional) request parameter */
    private long value;

    public PlayerControlEvent(PlayerControl request) {
        this.request = request;
    }

    public abstract boolean isConcerned(Player player);

    /** Control available for players */
    public static enum PlayerControl {
        PLAY, PAUSE, STOP, PREVIOUS, NEXT, JUMP_FORWARD, JUMP_BACK, JUMP_TO, TOGGLE_PAUSE;
    }
}
