package com.metlife.commons.util;

import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JsonUtils.
 * <description>
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 07/29/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public final class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * Constructor.
     */
    private JsonUtils() {

    }

    /**
     * Adds the list of maps inside the Json object.
     * @param jsonGenerator Json object
     * @param fieldName field name
     * @param list List of maps
     */
    public static void addListMap(final JsonGenerator jsonGenerator, final String fieldName,
            final List<Map<String, String>> list) {
        try {
            jsonGenerator.writeArrayFieldStart(fieldName);
            final Iterator<Map<String, String>> iterator = list.iterator();
            while (iterator.hasNext()) {
                jsonGenerator.writeStartObject();
                final Map<String, String> map = iterator.next();
                for (final Map.Entry<String, String> entry : map.entrySet()) {
                    jsonGenerator.writeStringField(entry.getKey(), entry.getValue());
                }
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        } catch (IOException e) {
            LOGGER.error("[AddListMap] : " + e.getMessage());
        }
    }

    /**
     * Adds the list of elements inside the Json object.
     * @param jsonGenerator json object
     * @param fieldName field name
     * @param list list of elements
     */
    public static void addList(final JsonGenerator jsonGenerator, final String fieldName, final List list) {
        try {
            jsonGenerator.writeArrayFieldStart(fieldName);
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                jsonGenerator.writeString(iterator.next());
            }
            jsonGenerator.writeEndArray();
        } catch (IOException e) {
            LOGGER.error("[AddList] : " + e.getMessage());
        }
    }
}
