package fr.dush.mediamanager.business.player;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import fr.dush.mediamanager.domain.media.Media;
import fr.dush.mediamanager.domain.media.MediaFile;
import fr.dush.mediamanager.events.EventBusRegisterEvent;
import fr.dush.mediamanager.events.play.PlayerCollectorEvent;
import fr.dush.mediamanager.events.play.PlayerControlEvent;
import fr.dush.mediamanager.exceptions.PlayerException;
import fr.dush.mediamanager.modulesapi.player.MetaPlayer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.UUID;

/** Register listener into Guava Events Bus and provide method to unregister it. */
public abstract class AbstractMetaPlayer<M extends Media, F extends MediaFile> implements MetaPlayer<M, F> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetaPlayer.class);

    @Getter
    protected String id;

    @Inject
    @Setter
    protected EventBus busEvent;

    public AbstractMetaPlayer() {
        id = generateId();
    }

    @PostConstruct
    public void registerItself() {
        busEvent.post(new EventBusRegisterEvent(this, true));
    }

    protected void unregisterItself() {
        busEvent.post(new EventBusRegisterEvent(this, false));
    }

    @Subscribe
    public void registerPlayer(PlayerCollectorEvent event) {
        LOGGER.debug("-->{}.registerPlayer({})", this.getClass(), event);
        if (isActive()) {
            event.registerPlayer(this);
        } else {
            unregisterItself();
        }
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    @Subscribe
    public void receivedControl(PlayerControlEvent event) throws PlayerException {
        LOGGER.debug("-->{}.receivedControl({})", this, event);

        if (event.isConcerned(this)) {
            handleControl(event.getRequest(), event.getValue());
        }

    }

    protected void handleControl(PlayerControlEvent.PlayerControl request, long value) throws PlayerException {
        switch (request) {
            case STOP:
                quit();
                break;

            case PLAY:
                play();
                break;

            case PAUSE:
            case TOGGLE_PAUSE:
                pause();
                break;

            case JUMP_FORWARD:
                seek(MoviePlayerWrapper.SEEK_DELAY);
                break;

            case JUMP_BACK:
                seek(-MoviePlayerWrapper.SEEK_DELAY);
                break;

            case JUMP_TO:
                setPosition(value);
                break;

            default:
                LOGGER.warn("Command {} is not available for Movie player.");
        }
    }
}
