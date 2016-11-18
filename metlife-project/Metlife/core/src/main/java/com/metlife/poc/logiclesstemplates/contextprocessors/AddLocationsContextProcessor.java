package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;


/**
 * AddLocationsContextProcessor.
 */
@Component
@Service
public class AddLocationsContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * FIND_AN_X_RESOURCE_TYPE.
     */
    public static final String FIND_AN_X_RESOURCE_TYPE =
            "MetlifeApp/components/section/find-an-x";
    /**
     * LOCATIONS_PATH_KEY_NAME.
     */
    public static final String LOCATIONS_PATH_KEY_NAME = "locationsPath";

    @Override
    public final String requiredResourceType() {
        return FIND_AN_X_RESOURCE_TYPE;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {

        final ResourceResolver resourceResolver = request.getResourceResolver();

        if (contentModel.has(Constants.CONTENT + Constants.DOT
                + LOCATIONS_PATH_KEY_NAME)) {

            //the page path where the locations will be stored. Should be
            // something lile /etc/metlifeDam/locations
            final String locationsPath = contentModel.getAsString(
                    Constants.CONTENT + Constants.DOT
                    + LOCATIONS_PATH_KEY_NAME);

            //Get the page resource from the path
            final Resource locationsPageResource = resourceResolver.getResource(
                    locationsPath);

            if (locationsPageResource != null) {

                //Get the parsys resource
                final Resource locationsContentResource = locationsPageResource
                        .getChild(Constants.JCR_CONTENT);

                if (locationsContentResource != null) {

                    final Resource locationsResource = locationsContentResource
                            .getChild(MetLifeConstants.LOCATIONS);

                    if (locationsResource != null) {

                        //The list where the location maps will be stored.
                        final List<Map<String, Object>> locationsList = Lists
                                .newArrayList();

                        //Get an iterator with the location resources
                        final Iterator<Resource> locationsIterator =
                                locationsResource.listChildren();

                        //Loop through the location resources, convert them
                        // to maps and add them to the list
                        while (locationsIterator.hasNext()) {
                            final Map<String, Object> locationMap =
                                    Maps.newHashMap(ResourceUtil.getValueMap(
                                            locationsIterator.next()));
                            locationsList.add(locationMap);
                        }

                        //Add the locations list to the content model
                        contentModel.set(Constants.CONTENT + Constants.DOT
                                + MetLifeConstants.LOCATIONS,
                                locationsList);

                    }
                }


            }
        }
    }

    //This Context Processor needs to run before the
    // AddNearLocationsContextProcessor
    @Override
    public final int priority() {
        final int ten = 10;
        return Constants.HIGH_PRIORITY + ten;
    }
}
