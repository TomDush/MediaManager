package fr.dush.mediacenters.modules.webui.rest.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/** @author Thomas Duchatelle */
public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectIdSerializer.class);

    @Override
    public void serialize(ObjectId value, JsonGenerator generator, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        generator.writeString(value.toString());
    }

    @Override
    public Class<ObjectId> handledType() {
        return ObjectId.class;
    }
}
