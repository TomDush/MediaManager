package fr.dush.mediamanager.modulesapi.player;

/**
 * @author Thomas Duchatelle
 */
public enum PlayerType {
    /** Use screen (image) */
    SCREEN(0x01),

    /** Use audio (music) */
    AUDIO(0x10),

    /** Use screen and audio (video) */
    BOTH(0x11);

    private final int mask;

    private PlayerType(int mask) {
        this.mask = mask;
    }

    public boolean hasConflict(PlayerType other) {
        return (mask & other.mask) != 0;
    }
}
