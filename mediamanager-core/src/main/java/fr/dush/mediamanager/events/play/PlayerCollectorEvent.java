package fr.dush.mediamanager.events.play;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import fr.dush.mediamanager.modulesapi.player.Player;

@Data
public class PlayerCollectorEvent {

    private List<Player> players = new ArrayList<>();

    public void registerPlayer(Player player) {
        players.add(player);
    }
}
