package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.xumak.base.Constants;

/**
 * Created by j.amorataya on 2/17/16.
 */

@Component
@Service
public class GetPropertyFromResourcePath extends
        AbstractResourceTypeCheckContextProcessor<TemplateContentModel> {

    /**
     * GET_QUOTE_TOOL_COMPONENT.
     */
    private static final String GET_QUOTE_TOOL_COMPONENT =
            "MetlifeApp/components/section/quote-tool";
    /**
     * RESOURCE_PROPERTY_PATH.
     */
    private static final String RESOURCE_PROPERTY_PATH =
            "xk_ResourcePathProperty";
    /**
     * RESOURCE_PROPERTY_NAME.
     */
    private static final String RESOURCE_PROPERTY_NAME =
            "xk_ResourcePropertyName";
    /**
     * PATH_TO_CONTAINER_FORM.
     */
    private static final String PATH_TO_CONTAINER_FORM =
            "/jcr:content/container/form";
    /**
     * DATA_PRODUCT_PROPERTY.
     */
    private static final String DATA_PRODUCT_PROPERTY = "dataProduct";

    /**
     * Get required resource types.
     * @return Set of strings.
     */
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(GET_QUOTE_TOOL_COMPONENT);
    }

    @Override
    public final void process(final SlingHttpServletRequest request,
                        final TemplateContentModel contentModel) throws
            Exception {

        //Get property path xk_ResourcePathProperty = content.path or content
        // .array:path
        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                + RESOURCE_PROPERTY_PATH)
                && contentModel.has(Constants.CONFIG_PROPERTIES_KEY
                + Constants.DOT
                + RESOURCE_PROPERTY_NAME)) {

            final ResourceResolver resourceResolver =
                    request.getResourceResolver();
            String propertyPath = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + RESOURCE_PROPERTY_PATH);
            String pathArray = Constants.BLANK;
            //Get propertyName xk_ResourcePropertyName = propertyName
            final String propertyName = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + RESOURCE_PROPERTY_NAME);

            if (propertyPath.contains(":")) {
                pathArray = propertyPath.substring(propertyPath.indexOf(':')
                        + 1, propertyPath.length());
                propertyPath = propertyPath.substring(0,
                        propertyPath.indexOf(':'));
            }
            //Get resource from property path
            if (!pathArray.isEmpty()) {
                final ArrayList<HashMap<String, String>> propertyArray =
                        (ArrayList<HashMap<String, String>>) Utils
                                .getPropertyAsList(contentModel, propertyPath);
                for (HashMap<String, String> element : propertyArray) {
                    if (element.containsKey(pathArray)) {
                        final String stringPath = element.get(pathArray);
                        final Resource resourcePath =
                                resourceResolver.getResource(
                                        stringPath + PATH_TO_CONTAINER_FORM);
                        if (resourcePath != null) {
                            //Get property
                            final ValueMap valueMapResource =
                                    resourcePath.adaptTo(ValueMap.class);
                            if (valueMapResource.containsKey(propertyName)) {
                                final String propertyValue = (String)
                                        valueMapResource.get(propertyName);
                                //atach in the same level path or array.
                                element.put(DATA_PRODUCT_PROPERTY,
                                        propertyValue);
                            }
                        }
                    }
                }


            }


        }

    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 1;
    } //must be executed after AddDeserializedJsonListContextProcessor

}
