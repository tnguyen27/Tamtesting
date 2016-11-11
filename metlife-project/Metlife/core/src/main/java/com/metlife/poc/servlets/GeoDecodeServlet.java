package com.metlife.poc.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.metlife.poc.util.MetLifeConstants;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Geodecoding servlet.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/02/08 | Jorge Hernandez       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@SlingServlet(
        label = "Geo Decoding Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        paths = "/bin/internal/geoDecoding",
        extensions = {MetLifeConstants.JSON})

public class GeoDecodeServlet extends SlingSafeMethodsServlet {
    /**
     * QUERY_ADDRESS.
     */
    public static final String QUERY_ADDRESS = "address";
    /**
     * LOGGER.
     */
    private final Logger lOG = LoggerFactory.getLogger(GeoDecodeServlet.class);
    /**
     * GOOGLE_MAPS_URL.
     */
    private static final String GOOGLE_MAPS_URL =
            "https://maps.googleapis"
                    + ".com/maps/api/geocode/json?address=%s&key=%s";
    /**
     * API_KEY.
     */
    private static final String API_KEY =
            "AIzaSyAa9kICWvBCNpUEv0rIGs87qD4b_aeyckg";

    // LITERALS
    /**
     * STATUS.
     */
    private static final String STATUS = "status";
    /**
     * RESULTS.
     */
    private static final String RESULTS = "results";
    /**
     * GEOMETRY.
     */
    private static final String GEOMETRY = "geometry";
    /**
     * LOCATION.
     */
    private static final String LOCATION = "location";
    /**
     * LAT.
     */
    private static final String LAT = "lat";
    /**
     * LONG.
     */
    private static final String LNG = "lng";
    /**
     * LATITUDE.
     */
    private static final String LATITUDE = "latitude";
    /**
     * LONGITUDE.
     */
    private static final String LONGITUDE = "longitude";

    @SuppressWarnings("unchecked")
    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {
        final int nineHundred = 900;
        final int oneThousand = 1000;
        try {
            lOG.info("Geodecoding servlet");
            final String address = URLEncoder.encode(request.getParameter(
                    QUERY_ADDRESS), "UTF-8");


            if (address != null) {
                Gson gson = new Gson();
                final Map<String, String> responseMap = new HashMap();
                final String url = String.format(GOOGLE_MAPS_URL, address,
                        API_KEY);

                final URL obj = new URL(url);
                final HttpURLConnection connection = (HttpURLConnection) obj
                        .openConnection();
                connection.setConnectTimeout(nineHundred);
                connection.setReadTimeout(oneThousand);

                // optional default is GET
                connection.setRequestMethod("GET");

                response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);
                final BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                final StringBuffer httpResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    httpResponse.append(inputLine);
                }
                in.close();
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Map.class, new
                        NaturalDeserializer());
                gson = gsonBuilder.create();
                final Map jsonMap = gson.fromJson(httpResponse.toString(),
                        Map.class);
                responseMap.put(STATUS, (String) jsonMap.get(STATUS));
                if (jsonMap.get(STATUS).equals("OK")) {
                    final ArrayList resultList =
                            (ArrayList) jsonMap.get(RESULTS);
                    if (resultList.size() == 1) {
                        final Map result = (Map) resultList.get(0);
                        final Map geometry = (Map) result.get(GEOMETRY);
                        final Map<String, String> location = (Map) geometry.get(
                                LOCATION);
                        responseMap.put(LATITUDE, location.get(LAT));
                        responseMap.put(LONGITUDE, location.get(LNG));

                    }
                }
                response.getWriter().write(gson.toJson(responseMap));
            }
        } catch (Exception e) {
            e.printStackTrace(response.getWriter());
            //.write();
        }
    }

    /**
     * NaturalDeserializer class.
     */
    private static class NaturalDeserializer implements
            JsonDeserializer<Object> {
        /**
         * Deserialize.
         * @param json JsonElement.
         * @param typeOfT Type.
         * @param context JsonDeserializationContext.
         * @return Object.
         */
        public Object deserialize(final JsonElement json, final Type typeOfT,
                                  final JsonDeserializationContext context) {


            final Object deserializeResult;
            if (json.isJsonNull()) {
                deserializeResult = null;
            } else if (json.isJsonPrimitive()) {
                deserializeResult = handlePrimitive(json.getAsJsonPrimitive());
            } else if (json.isJsonArray()) {
                deserializeResult = handleArray(json.getAsJsonArray(), context);

            } else {
                deserializeResult =
                        handleObject(json.getAsJsonObject(), context);
            }
            return deserializeResult;
        }

        /**
         * Handle primitive.
         * @param json JsonPrimitive.
         * @return Object.
         */
        private Object handlePrimitive(final JsonPrimitive json) {

            Object handlePrimitiveResult;
            if (json.isBoolean()) {
                handlePrimitiveResult = json.getAsBoolean();
            } else if (json.isString()) {
                handlePrimitiveResult = json.getAsString();

            } else {
                final BigDecimal bigDec = json.getAsBigDecimal();
                // Find out if it is an int type
                try {
                    bigDec.toBigIntegerExact();
                    try {

                        handlePrimitiveResult = bigDec.intValueExact();
                    } catch (ArithmeticException ignored) {
                        handlePrimitiveResult = bigDec.longValue();
                    }

                } catch (ArithmeticException ignored) {
                    handlePrimitiveResult = bigDec.doubleValue();
               }
                // Just return it as a double
            }
            return handlePrimitiveResult;
        }

        /**
         * Handle array.
         * @param json JsonArray.
         * @param context JsonDeserializationContext.
         * @return Object.
         */
        private Object handleArray(final JsonArray json,
                                   final JsonDeserializationContext context) {
            final Object[] array = new Object[json.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = context.deserialize(json.get(i), Object.class);
            }
            return array;
        }

        /**
         * Handle object.
         * @param json JsonObject.
         * @param context JsonDeserializationContext.
         * @return Object.
         */
        private Object handleObject(final JsonObject json,
                                    final JsonDeserializationContext context) {
            final Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                map.put(entry.getKey(), context.deserialize(entry.getValue(),
                        Object.class));
            }
            return map;
        }
    }

}
