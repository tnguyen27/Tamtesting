package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Haversine;
import com.metlife.poc.util.SelectorUtils;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;

/**
 * AddNearLocationsContextProcessor.
 */
@Component
@Service
public class AddNearLocationsContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * BREADCRUMB_RESOURCE_TYPE.
     */
    public static final String BREADCRUMB_RESOURCE_TYPE =
            "MetlifeApp/components/section/find-an-x";

    /**
     * PARAMETER_NAMES.
     */
    public static final Set<String> PARAMETER_NAMES = Sets.newHashSet(
            MetLifeConstants.LATITUDE,
            MetLifeConstants.LONGITUDE,
            MetLifeConstants.RADIUS,
            MetLifeConstants.SPECIALTY
    );

    @Override
    public final String requiredResourceType() {
        return BREADCRUMB_RESOURCE_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {

        final int five = 5;

        //Get selectors from the request
        final String[] selectors = request.getRequestPathInfo().getSelectors();

        //make sure the request has the correct number of parameters and the
        // first one is "proximity",
        //that way we are sure the proximity servlet is going to be used to
        // render the response
        if (selectors.length == five && MetLifeConstants.PROXIMITY.
                equals(selectors[0])) {

            final Map<String, String> params = SelectorUtils.parseParams(
                    selectors);

            //We have to make sure the parameters sent are the parameters
            // needed.
            //If there are parameters missing, we won't add anything to the
            // content model.
            if (params.keySet().containsAll(PARAMETER_NAMES) && contentModel
                    .has(Constants.CONTENT + Constants.DOT
                            + MetLifeConstants.LOCATIONS)) {

                //Get the list of locations
                final List<Map<String, Object>> locations =
                        (List<Map<String, Object>>) contentModel.get(
                                Constants.CONTENT
                                + Constants.DOT + MetLifeConstants.LOCATIONS);

                final List<Map<String, Object>> nearLocations =
                        Lists.newArrayList();

                for (Map<String, Object> location : locations) {

                    if (location.containsKey(MetLifeConstants.SPECIALTIES)) {
                        final List<String> locationSpecialties = Arrays.asList(
                                (String[]) location.get(
                                        MetLifeConstants.SPECIALTIES));

                        if (locationSpecialties.contains(params.get(
                                MetLifeConstants.SPECIALTY))) {

                            if (location.containsKey(
                                    MetLifeConstants.LATITUDE) && location
                                    .containsKey(MetLifeConstants.LONGITUDE)) {

                                //Get the lat and long of the location
                                final double locationLatitude = NumberUtils
                                        .toDouble((String) location.get(
                                                MetLifeConstants.LATITUDE));
                                final double locationLongitude = NumberUtils
                                        .toDouble((String) location.get(
                                                MetLifeConstants.LONGITUDE));

                                //Sad, sad workaround because sling decodes
                                // the dots on selectors
                                //so they get recognized as separators,
                                // making additional unwanted selectors :(
                                final String userLatString = params.get(
                                        MetLifeConstants.LATITUDE).replace(
                                        MetLifeConstants.COMMA, Constants.DOT);
                                final String userLongString = params.get(
                                        MetLifeConstants.LONGITUDE).replace(
                                        MetLifeConstants.COMMA, Constants.DOT);

                                //Get the lat and long from the request
                                final double userLatitude =
                                        NumberUtils.toDouble(userLatString);
                                final double userLongitude =
                                        NumberUtils.toDouble(userLongString);

                                //Get the radius from the request
                                final double radius =
                                        NumberUtils.toDouble(params.get(
                                                MetLifeConstants.RADIUS));

                                final String units = contentModel.getAsString(
                                        Constants.CONTENT + Constants.DOT
                                                + MetLifeConstants.UNITS);

                                //Get the distance between the two locations
                                final double distance = Haversine.distance(
                                        locationLatitude,
                                        locationLongitude,
                                        userLatitude,
                                        userLongitude,
                                        units
                                );

                                //If the distance is less than the radius,
                                // then it is 'near' and we add it to the list.
                                if (distance < radius) {

                                    //we will need to return the distance on
                                    // the service
                                    location.put(MetLifeConstants.DISTANCE,
                                            String.valueOf(
                                            distance));

                                    nearLocations.add(location);
                                }

                            }
                        }
                    }
                }

                //Add the near locations list to the content model
                contentModel.set(Constants.CONTENT + Constants.DOT
                        + MetLifeConstants.NEAR_LOCATIONS, nearLocations);
            }

        }
    }


}
