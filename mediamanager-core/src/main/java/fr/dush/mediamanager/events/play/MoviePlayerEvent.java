package fr.dush.mediamanager.events.play;

import fr.dush.mediamanager.domain.media.video.Movie;
import lombok.Getter;

/** Event from video player playing a movie */
@Getter
public class MoviePlayerEvent extends PlayerEvent {

    private final Movie movie;

    public MoviePlayerEvent(PlayerEvent event, Movie movie) {
        super(event.getPlayer(), event.getType(), event.getPosition(), event.getLength(), event.getMedias());
        this.movie = movie;
    }
}
