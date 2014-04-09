package fr.dush.mediamanager.events.play;

import lombok.Data;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import fr.dush.mediamanager.modulesapi.player.MetaPlayer;
import fr.dush.mediamanager.modulesapi.player.Player;

/** Get a player with its ID */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerControlEventById extends PlayerControlEvent {

    /** Player ID concerned by this event */
    private String target;

    public PlayerControlEventById(PlayerControl request, long value, String target) {
        super(request, value);
        this.target = target;
    }

    public PlayerControlEventById(PlayerControl request, String target) {
        super(request);
        this.target = target;
    }

    @Override
    public boolean isConcerned(Player player) {
        if (player instanceof MetaPlayer) {
            return StringUtils.equals(((MetaPlayer) player).getId(), target);
        }

        return false;
    }
}
