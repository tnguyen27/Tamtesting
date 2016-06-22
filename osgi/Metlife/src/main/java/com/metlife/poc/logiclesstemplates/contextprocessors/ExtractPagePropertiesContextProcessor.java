package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.collections.MapUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * ExtractPagePropertiesContextProcessor
 *
 * Extract properties from "page" context to "content" context. Use
 * 'xk_extractPageProperties'
 * to specify the name of the property(s) that should be extracted.  All
 * COLONS in property names
 * will be replaced by UNDERSCORES.
 *
 * xk_extractPageProperties:
 * [property_name]
 *
 * Example:
 * xk_extractPageProperties:
 * cq:lastReplicated
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 08/02/2016 | Pablo García    | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class ExtractPagePropertiesContextProcessor extends
        AbstractConfigurationContextProcessor {
    /**
     * XK_PAGE_PROPERTY_NAME.
     */
    public static final String XK_PAGE_PROPERTY_NAME =
            "xk_extractPageProperties";

    @Override
    public final boolean mustExist() {
        return false;
    }

    /**
     * Get required property names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_PAGE_PROPERTY_NAME);
    }

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Get list of key names.
     * @param resource Resource.
     * @return Collection of strings.
     * @throws Exception throws exception.
     */
    public final Collection<String> getListKeyNames(final Resource resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final Collection<String> congifurationListsKeyNames =
                configuration.asStrings(XK_PAGE_PROPERTY_NAME);
        return congifurationListsKeyNames;
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel)
            throws Exception {
        final Resource resource = request.getResource();
        final Map<String, Object> configObject = (Map<String, Object>)
                contentModel
                .get(Constants.CONFIG_PROPERTIES_KEY);
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        final Map<String, Object> pageObject =
                (Map<String, Object>) contentModel
                .get(Constants.GLOBAL_PAGE_CONTENT_KEY);

        if (configObject.containsKey(XK_PAGE_PROPERTY_NAME)) {
            final Iterator<String> keys = configObject.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                if (key.contains(XK_PAGE_PROPERTY_NAME)) {

                    final Collection<String> configProperties = getListKeyNames(
                            resource);

                    final Iterator<String> propsIterator = configProperties
                            .iterator();
                    while (propsIterator.hasNext()) {
                        final String propName = propsIterator.next();
                        final String newPropertyName = propName.replace(
                                MetLifeConstants.COLON,
                                MetLifeConstants.UNDERSCORE);
                        final Object propValue = MapUtils.getObject(pageObject,
                                propName);
                        contentObject.put(newPropertyName, propValue);
                    }
                }
            }
        }
    }
}
