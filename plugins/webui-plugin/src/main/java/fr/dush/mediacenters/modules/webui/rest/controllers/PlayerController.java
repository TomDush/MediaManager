package fr.dush.mediacenters.modules.webui.rest.controllers;

import fr.dush.mediacenters.modules.webui.rest.dto.MovieInfo;
import fr.dush.mediacenters.modules.webui.rest.dto.PlayerInfo;
import fr.dush.mediamanager.events.play.MoviePlayRequestEvent;
import fr.dush.mediamanager.events.play.PlayerCollectorEvent;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.events.play.PlayerControlEventById;
import fr.dush.mediamanager.modulesapi.player.MetaPlayer;
import fr.dush.mediamanager.modulesapi.player.Player;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

// TODO should review paths:
// - /players/play/<type>/<mediaId>/<videoId>
// - /players/playing -> players currently playing something
// - /players/ctrl/<playerId>/<action>?value=<actionValue>

@RequestScoped
@Path("/players")
public class PlayerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

    @Inject
    private Mapper mapper;

    @Inject
    private Event<MoviePlayRequestEvent> playBus;
    @Inject
    private Event<PlayerCollectorEvent> collectorBus;
    @Inject
    private Event<PlayerControlEvent> controlBus;

    @Path("/play/{type:\\w+}/{mediaId}/{path}")
    public String play(@PathParam("type") String type, @PathParam("mediaId") String mediaId,
                       @PathParam("path") String path) {

        LOGGER.debug("Play request: [type={} ; mediaId={} ; path={}]", type, mediaId, path);

        try {
            playBus.fire(new MoviePlayRequestEvent(mediaId, path));
            return "{play: 1}";

        } catch (Exception e) {
            LOGGER.error("Can't play movie {} [{}]", path, mediaId, e);
            return "{play: 0}";
        }

    }

    @GET
    @Path("/playing.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PlayerInfo> getPlaysInProgress() {
        try {
            // Send event to collect every player in use
            PlayerCollectorEvent event = new PlayerCollectorEvent();
            collectorBus.fire(event);

            // Convert players
            ArrayList<PlayerInfo> players = new ArrayList<>();

            for (Player player : event.getPlayers()) {
                PlayerInfo info = new PlayerInfo();
                info.setPosition(player.getPosition());
                info.setLength(player.getTotalLength());
                info.setPaused(player.isPaused());

                if (player instanceof MetaPlayer) {
                    MetaPlayer<?, ?> metaPlayer = (MetaPlayer<?, ?>) player;
                    info.setId(metaPlayer.getId());
                    info.setMedia(mapper.map(metaPlayer.getMedia(), MovieInfo.class));
                }

                players.add(info);
            }

            return players;

        } catch (Exception e) {
            LOGGER.error("Couldn't retrieve players. ", e);
            return new ArrayList<>();
        }
    }

    @GET
    @Path("/ctrl/{id}/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    public String controlPlayer(@PathParam("id") String id, @PathParam("action") String action) {
        try {
            controlBus.fire(new PlayerControlEventById(PlayerControlEvent.PlayerControl.valueOf(action), id));
            return "{player: 1}";

        } catch (Exception e) {
            LOGGER.error("Couldn't control player: [id={} ; action={}]", id, action);
            return "{player: 0}";
        }
    }

}
