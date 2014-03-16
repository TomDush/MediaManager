package fr.dush.mediamanager.business.mediatech;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event to register an art repository.
 */
@Getter
@AllArgsConstructor
public class ArtRepositoryRegisterEvent {

    /** Repository identifier (used in art reference) */
    private String name;

    private ArtRepository artRepository;

}
