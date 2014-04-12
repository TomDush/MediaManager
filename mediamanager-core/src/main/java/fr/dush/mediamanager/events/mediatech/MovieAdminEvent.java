package fr.dush.mediamanager.events.mediatech;

import lombok.Data;

/**
 * @author Thomas Duchatelle
 */
@Data
public class MovieAdminEvent extends AdminEvent {

    /** Movie ID */
    private String id;

    /** Operation optional value */
    private String value;

    public MovieAdminEvent(Object source, Operation operation, String id) {
        super(source, operation);
        this.id = id;
    }

    public MovieAdminEvent(Object source, Operation operation, String id, String value) {
        super(source, operation);
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "MovieAdminEvent{" +
               "id='" + id + '\'' +
               ", operation='" + getOperation() + '\'' +
               ", value='" + value + '\'' +
               '}';
    }
}
