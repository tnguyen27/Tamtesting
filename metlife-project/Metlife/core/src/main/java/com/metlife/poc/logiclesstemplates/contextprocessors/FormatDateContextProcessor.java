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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * FormatDateContextProcessor
 *
 * Provide the desired date format to a property. Use 'xk_formatDate'
 * to specify the name of the property(s) that should formatted.
 *
 * xk_formatDate:
 * [property_name]
 *
 * Example:
 * xk_formatDate:
 * cq_lastReplicated
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 09/02/2016 | Pablo García    | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class FormatDateContextProcessor extends
        AbstractConfigurationContextProcessor {
    /**
     * XK_DATE_TO_FORMAT_PROPERTY_NAME.
     */
    public static final String XK_DATE_TO_FORMAT_PROPERTY_NAME =
            "xk_formatDate";

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        final int ten = 10;
        return Constants.LOW_PRIORITY - ten;
        //must be executed after any other context
        // processor
    }

    /**
     * Get required property names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_DATE_TO_FORMAT_PROPERTY_NAME);
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
                configuration.asStrings(XK_DATE_TO_FORMAT_PROPERTY_NAME);
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
        final Map<String, Object> configObject =
                (Map<String, Object>) contentModel
                .get(Constants.CONFIG_PROPERTIES_KEY);
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);

        if (configObject.containsKey(XK_DATE_TO_FORMAT_PROPERTY_NAME)) {
            final Iterator<String> keys = configObject.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                if (key.contains(XK_DATE_TO_FORMAT_PROPERTY_NAME)) {

                    final  Collection<String> configProperties =
                            getListKeyNames(resource);

                    final Iterator<String> propsIterator = configProperties
                            .iterator();
                    while (propsIterator.hasNext()) {
                        final String propertyName = propsIterator.next();
                        final String[] propertyToFormatSplitted = propertyName
                                .split(MetLifeConstants.DOUBLE_COLON);
                        if (propertyToFormatSplitted.length < 2) {
                            throw new Exception("xk_formatDate is wrong.");
                        }

                        final String propertyToFormat =
                                propertyToFormatSplitted[0];
                        final String propertyFormat =
                                propertyToFormatSplitted[1];


                        try {
                            final Calendar dateProperty = (Calendar) MapUtils
                                    .getObject(contentObject, propertyToFormat);
                            final Date date = dateProperty.getTime();
                            final SimpleDateFormat format =
                                    new SimpleDateFormat(propertyFormat);
                            final String formatted = format.format(date);
                            contentObject.put(propertyToFormat, formatted);
                        } catch (Exception e) {
                            this.log.error("Error parsing date {}", e
                                    .getMessage());
                        }

                    }
                }
            }
        }
    }
}
