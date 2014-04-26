package fr.dush.mediamanager.plugins.webui.rest.dto;

import lombok.Data;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.io.Serializable;

/**
 * Information on requested page.
 *
 * @author Thomas Duchatelle
 */
@Data
public class Pagination implements Serializable {

    /** Index page, BE CAREFUL, <b>starting by 1</b> ! */
    @QueryParam("index")
    @DefaultValue("0")
    private int index = 0;

    /** Page size, default is 10 */
    @QueryParam("pageSize")
    @DefaultValue("10")
    private int pageSize;
}
