package fr.dush.mediacenters.modules.webui.rest.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/** @author Thomas Duchatelle */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMapperProducer implements ContextResolver<ObjectMapper> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProducer.class);

    private ObjectMapper objectMapper;

    public ObjectMapperProducer() {
        objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule("WebUISerializer");
        module.addSerializer(new ObjectIdSerializer());

        objectMapper.registerModule(module);
    }

    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return objectMapper;
    }
}
