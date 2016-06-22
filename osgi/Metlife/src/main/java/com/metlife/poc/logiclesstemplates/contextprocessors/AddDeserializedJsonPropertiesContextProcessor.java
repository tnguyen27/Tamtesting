package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.metlife.poc.util.JSONUtils;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.configuration.Mode;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Set;

/**
 * AddListPropertiesContextProcessor
 * Deserializes a Json using the xk_deserializeJSONProperties properties
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 13/03/2015 | Lesly Quiñonez        | Initial Creation.
 * 2.0     | 26/01/2016 | J. Alejandro Morataya | Adapt to use on Metlife
 * Project
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class AddDeserializedJsonPropertiesContextProcessor extends
        AbstractResourceTypeCheckContextProcessor {


    /**
     * XK_LIST_PROPERTIES.
     */
    public static final String XK_LIST_PROPERTIES =
            "xk_deserializeJSONProperties";

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    @Override
    public final Set<String> requiredResourceTypes() {
        return defaultResourceTypes;
    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY;
    }

    @Override
    public final boolean mustExist() {
        return false;
    }


    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                request.getResource().getResourceType());
        final Collection<String> propsWithList = configuration.asStrings(
                XK_LIST_PROPERTIES, Mode.MERGE);
        for (String propName : propsWithList) {
            if (contentModel.has(propName)) {
                final Object deserializeJson = Utils.getPropertyAsList(
                        contentModel, propName);
                contentModel.set(propName, JSONUtils
                        .jsonStringObjectToMapList(deserializeJson));
            }
        }
    }


}

