package com.metlife.poc.servlets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**
 * Proximity Servlet.
 * Example request:
 * /find_an_x_page_path/find_an_x.proximity.latitude=14,5944267
 * .longitude=-90,5175378.radius=10.specialty=general.json
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2016/02/01 | Palecio       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@SlingServlet(
        label = "Proximity Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        resourceTypes = {ProximityServlet.FIND_AN_X_RESOURCE_TYPE},
        selectors = {MetLifeConstants.PROXIMITY},
        extensions = {MetLifeConstants.JSON})
@Properties({
        @Property(name = "service.description", value = "MetLife Proximity "
                + "Servlet")
})
public class ProximityServlet extends SlingAllMethodsServlet {

    /**
     * Logger.
     */
    private final Logger lOG = LoggerFactory.getLogger(ProximityServlet.class);
    /**
     * FIND_AN_X_RESOURCE_TYPE.
     */
    public static final String FIND_AN_X_RESOURCE_TYPE =
            "MetlifeApp/components/section/find-an-x";

    /**.
     * Map with the properties required on the JSON and the property names
     * that store the values in the repo
     * To add another property to the JSON, just put an additional entry on
     * the map
     */
    private static final ImmutableMap<String, String> LOCATION_PROPERTIES_MAP =
            new ImmutableMap.Builder<String, String>()
                    .put("fclt_name", "officeName")
                    .put("fclt_addr", "address")
                    .put("fclt_city", "city")
                    .put("fclt_state", "state")
                    .put("fclt_zip", "zip")
                    .put("tel_no", "phone")
                    .put("fclt_url", "url")
                    .put("fclt_lattd", "latitude")
                    .put("fclt_longtd", "longitude")
                    .put("fclt_ctgy", "specialties")
                    .put("fclt_distance", "distance")
                    .put("fclt_alt_phone", "alternatePhone")
                    .put("fclt_fax", "fax")
                    .put("fclt_gender", "gender")
                    .put("fclt_languages", "languages")
                    .put("fclt_education", "education")
                    .put("fclt_email", "email")
                    .put("fclt_secondary_email", "secondaryEmail")
                    .put("fclt_main_contact", "mainContact")
                    .put("fclt_hours", "workingHours")
                    .put("fclt_info", "additionalInfo")
                    .build();
    /**
     * List with the labels for the Find an X component.
     */
    private static final ImmutableList<String> LABEL_KEYS_LIST = new
            ImmutableList.Builder<String>()
            .add("phoneLabel")
            .add("alternatePhoneLabel")
            .add("faxLabel")
            .add("emailLabel")
            .add("secondaryEmailLabel")
            .add("mainContactLabel")
            .add("hoursLabel")
            .add("additionalInfoLabel")
            .add("units")
            .build();


    @SuppressWarnings("unchecked")
    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {
        lOG.info("proximity servlet");

        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                      TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);

        if (contentModel.has(Constants.CONTENT + Constants.DOT
                + MetLifeConstants.NEAR_LOCATIONS)) {
            //The list of near locations.
            final List<Map<String, Object>> nearLocations =
                    (List<Map<String, Object>>) contentModel.get(
                            Constants.CONTENT
                            + Constants.DOT + MetLifeConstants.NEAR_LOCATIONS);

            //The list of near locations that we're going to send in the
            // response.
            //It has the format needed by the javascript to display the
            // locations.
            final List formattedLocationsList = formatLocations(nearLocations,
                    getLabels(contentModel));

            //The js expects the locations list to be wrapped into a
            // 'facilities' object...
            final Map facilities = Maps.newHashMap();
            facilities.put(MetLifeConstants.FACILITIES, formattedLocationsList);

            //set application/json as the response content type
            response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

            //Write the json to the response
            response.getWriter().write(new Gson().toJson(facilities));
        }
    }

    /**
     * @param locations          a list of 'raw' locations
     * @param constantProperties these properties are going to be added to
     *                           all of the formatted locations
     * @return a list of formatted locations ready to be written to the response
     */
    private List<Map<String, String>> formatLocations(final List<Map<String,
            Object>> locations,
                                                      final Map<String, String>
            constantProperties) {

        String locationValue;

        final List<Map<String, String>> formattedLocationsList =
                Lists.newArrayList();


        Collections.sort(locations, new LocationComparator());
        for (Map<String, Object> location : locations) {

            final Map<String, String> formattedLocation = Maps.newHashMap();

            for (Map.Entry<String, String> property : LOCATION_PROPERTIES_MAP
                    .entrySet()) {

                //Only put the properties that have values
                if (location.containsKey(property.getValue())) {
                    if (property.getKey().toString().equals("fclt_ctgy")) {
                        final String value = StringUtils.join(Arrays.toString(
                                (Object[]) location.get(property.getValue())),
                                ",");
                        locationValue = value.substring(1, value.length() - 2);
                    } else {
                        locationValue = location.get(property.getValue())
                                .toString();
                    }
                    formattedLocation.put(property.getKey(), locationValue);
                }
            }

            //Add the constant properties
            formattedLocation.putAll(constantProperties);
            //Add the location to the list
            formattedLocationsList.add(formattedLocation);
        }
        return formattedLocationsList;
    }

    /**
     * Returns a map of labels based on the LABEL_KEYS_LIST.
     *
     * @param contentModel THE content model
     * @return a map of labels
     */
    private Map<String, String> getLabels(final TemplateContentModel
                                                  contentModel) {
        final Map<String, String> labelsMap = Maps.newHashMap();

        for (String key : LABEL_KEYS_LIST) {
            if (contentModel.has(Constants.CONTENT + Constants.DOT + key)) {
                labelsMap.put(key, contentModel.getAsString(Constants.CONTENT
                        + Constants.DOT
                        + key));
            }
        }
        return labelsMap;
    }

    /**
     *  LocationComparator class.
     */
    private class LocationComparator implements Comparator<Map<String,
            Object>> {
        @Override
        public int compare(final Map<String, Object> location1,
                           final Map<String, Object> location2) {
            int comp = 0;
            if (location1.containsKey(MetLifeConstants.DISTANCE)
                    && location2.containsKey(
                    MetLifeConstants.DISTANCE)) {
                final double distance1 = NumberUtils.toDouble((String) location1
                        .get(MetLifeConstants.DISTANCE));
                final double distance2 = NumberUtils.toDouble((String) location2
                        .get(MetLifeConstants.DISTANCE));
                if (distance1 < distance2) {
                    comp = -1;
                } else if (distance1 == distance2) {
                    comp = 0;
                } else {
                    comp = 1;
                }
                /*
                comp = distance1 < distance2
                        ? -1 : distance1 == distance2
                        ? 0 : 1;
                        */
            }
            return comp;
        }
    }
}
