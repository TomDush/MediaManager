package fr.dush.mediamanager.dao.mongodb.convertors;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert Java date to BSON dates : <code> { creation : {$date : '2013-12-20T11:28:41.852Z'} }</code> instead of :
 * <code>{ creation : '2013-12-20T11:28:41.852Z' } </code>
 */
public class BsonDateSerializer extends StdSerializer<Date> {

    public BsonDateSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeFieldName("$date");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        jgen.writeString(formatter.format(value));
        jgen.writeEndObject();
    }
}
