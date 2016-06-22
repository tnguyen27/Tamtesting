package com.metlife.poc.util;

import com.google.common.collect.Lists;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jhernandez on 12/30/15.
 */
public final class JSONUtils {
    /**
     * JSON Utils.
     */
    private JSONUtils() {
    }

    /**
     * Some widgets store their values as a list of json objects (like
     * multifield widget).
     * This method takes these json objects, looks for a property on these
     * objects and returns
     * the list of values.
     *
     * @param jsonList     a list of json string objects
     * @param jsonProperty property to look in json objects
     * @return list of property values
     */
    public static List<String> jsonPropertyToList(final List<String>
                                                          jsonList, final
    String jsonProperty) {
        final List<String> stringList = new ArrayList();
        if (null != jsonList) {
            for (String jsonString : jsonList) {
                try {
                    final JSONObject obj = new JSONObject(jsonString);
                    if (obj.has(jsonProperty)) {
                        stringList.add(obj.getString(jsonProperty));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringList;
    }

    /**
     * Converts a  JSON string into a Map.
     *
     * @param jsonStringObject raw json string
     * @return map
     */
    public static Map jsonStringObjectToMap(final String jsonStringObject) {
        //for each json object, parse it and put the properties in a map
        final HashMap<String, Object> properties = new HashMap();
        try {
            final JSONObject jsonObject = new JSONObject(jsonStringObject);
            final Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONArray) {
                    final JSONArray array = (JSONArray) value;
                    final List<Object> tempList = Lists.newArrayList();
                    for (int i = 0; i < array.length(); i++) {
                        tempList.add(array.get(i));
                    }
                    value = tempList;
                }
                properties.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Converts a list of json strings into a List of Maps.
     *
     * @param jsonStringObjectList List object with raw json string
     * @return List of maps
     */
    public static List<Map> jsonStringObjectListToMapList(
            final List<String> jsonStringObjectList) {
        //for each property name, get it's value (list of json string objects)
        final ArrayList<Map> listOfProperties = new ArrayList();
        if (jsonStringObjectList != null) {
            //for each string object, convert it to a map of properties
            for (String jsonStringObject : jsonStringObjectList) {
                final Map properties = jsonStringObjectToMap(jsonStringObject);
                listOfProperties.add(properties);
            }
        }
        return listOfProperties;
    }

    /**
     * Convert jsonObject into a list of maps.
     *
     * @param jsonObject JSON Object
     * @return List of maps
     */
    public static List<Map> jsonStringObjectToMapList(final Object jsonObject) {
        List<Map> items = new ArrayList<>();
        if (jsonObject instanceof ArrayList) {
            items = JSONUtils.jsonStringObjectListToMapList(
                    (ArrayList<String>) jsonObject);
        } else if (jsonObject instanceof String) {
            items.add(
                    JSONUtils.jsonStringObjectToMap(jsonObject.toString()));
        }

        return items;
    }

    /**
     * Get json from buffer reader.
     *
     * @param reader Buffer Reader
     * @return a json object
     */
    public static JSONObject getJsonFromReader(final BufferedReader reader) {

        JSONObject getJsonFromReaderResult;
        final StringBuffer jb = new StringBuffer();
        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

        } catch (Exception e) {

            getJsonFromReaderResult = null;
            /*report an error*/
        }


        try {
            final JSONObject jsonObject1 = new JSONObject(jb.toString());
            getJsonFromReaderResult = jsonObject1;
        } catch (JSONException e) {
            e.printStackTrace();
            getJsonFromReaderResult = null;
        }
        return getJsonFromReaderResult;

    }
}
