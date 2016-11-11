package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.xumak.base.Constants;
import com.xumak.base.configuration.Mode;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * AddCountListPropertiesContextProcessor
 * Count a list of properties
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 30/05/2016 | Roger Jimenez         | Initial Creation.
 * Project
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class AddCountListPropertiesContextProcessor  extends
        AbstractResourceTypeCheckContextProcessor {
    /**
     * Count properties.
     */
    public static final String COUNT_PROPERTIES_KEY = "countProperties";

    /**
     * XK_LIST_PROPERTIES.
     */
    public static final String XK_LIST_PROPERTIES =
            "xk_countListProperties";

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

        /**
         * contentObject
         */
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);

        final XCQBConfiguration configuration = configurationProvider.getFor(
                request.getResource().getResourceType());
        final Collection<String> propsWithList = configuration.asStrings(
                XK_LIST_PROPERTIES, Mode.MERGE);
        int countProperties = 0;
        for (String propName : propsWithList) {
            if (contentModel.has(propName)) {
                countProperties++;
            }
        }
        if (countProperties > 0) {
            contentObject.put(COUNT_PROPERTIES_KEY, countProperties);
        }
    }

}
