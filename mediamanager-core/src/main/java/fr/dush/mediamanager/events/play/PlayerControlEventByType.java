package fr.dush.mediamanager.events.play;

import lombok.Data;
import fr.dush.mediamanager.modulesapi.player.Player;
import fr.dush.mediamanager.modulesapi.player.PlayerType;
import lombok.EqualsAndHashCode;

/** Control every player using a resource type (Video, Audio, Both) */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerControlEventByType extends PlayerControlEvent {

    /** Player ID concerned by this event */
    private PlayerType type;

    public PlayerControlEventByType(PlayerControl request, long value, PlayerType type) {
        super(request, value);
        this.type = type;
    }

    public PlayerControlEventByType(PlayerControl request, PlayerType type) {
        super(request);
        this.type = type;
    }

    @Override
    public boolean isConcerned(Player player) {
        return type.hasConflict(player.getType());
    }
}
