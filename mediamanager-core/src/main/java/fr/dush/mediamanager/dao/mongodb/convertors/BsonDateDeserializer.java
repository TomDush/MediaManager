package fr.dush.mediamanager.dao.mongodb.convertors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Deserialize Bson date */
public class BsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode tree = jp.readValueAsTree();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            return formatter.parse(tree.get("$date").toString());
        } catch (Exception e) {
            throw new IOException("Can't parse : " + tree, e);
        }
    }
}
