package com.metlife.global.microsites.logiclesstemplates.processors;

import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.global.microsites.util.ContextProcessorUtils;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.JSONUtils;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * CopyPageConfigurationContextProcessor.
 *
 * This context processor allows you to copy nodes or properties from a parent
 * page, the configuration is set through the xk_copyPageConfiguration property.
 *
 * Example:
 * xk_copyPageConfiguration: [
 *          node::metlifeMicrosites/components/page/homepage::logo-metlife,
 *          property::metlifeMicrosites/components/page/homepage::logoLinkMetLife
 *          ]
 *
 * In this example the context processor will execute the next tasks:
 * 1) It will search a parent page of the type metlifeMicrosites/components/page/homepage
 *    (This can be different types of page, the only requirement is that the referenced page
 *    is a parent of the destination page)
 *
 * 2) If found, it will look at the first parameter "node" or "property"
 *    if it is a node it will search for the node ("logo-metlife") in the parent page
 *    if it is a property it will search for the property ("logoLinkMetlife") in the parent page
 *
 * 3) If the node or property is found
 *
 *
 *
 *      a.1) if it is a node it will search for a node with the same name in
 *           the destination page
 *      a.2) if a node with the same name already exists in the destination page
 *           it will delete it
 *      a.3) it will copy the referenced node to the destination resource
 *
 *
 *
 *      b.1) if it is a property it will copy the property and its value to
 *          the content model of the destination page
 *
 *
 *
 * NOTE: the destination page is the page where the xk_copyPageConfiguration
 *       is set.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 04/07/2016 | Diego Tello     | Initial creation.
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class CopyPageConfigurationContextProcessor
        extends AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * XK_COPYPAGECONFIGURATION.
     */
    public static final String XK_COPYPAGECONFIGURATION = "xk_copyPageConfiguration";

    /**
     * PRIORITY.
     */
    public static final int PRIORITY = Constants.LOW_PRIORITY;

    /**
     * Constant to identify if the resource to copy is a node.
     */
    private static final String NODE = "node";

    /**
     * Constant to identify if the resource to copy is a property.
     */
    private static final String PROPERTY = "property";

    /**
     * Illegal format exception message.
     */
    private static final String ILLEGALFORMATMESSAGE = "The configuration value does not comply with the"
            + " required format (node|property)::resourceType::name";

    /**
     * No such element exception message.
     */
    private static final String NOSUCHELEMENTMESSAGE = "The referenced resource was not found.";

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(CopyPageConfigurationContextProcessor.class);

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_COPYPAGECONFIGURATION);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList(XK_COPYPAGECONFIGURATION);
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest,
            final ContentModel contentModel) throws Exception {
        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + XK_COPYPAGECONFIGURATION)) {
            final Collection<String> configurations = Utils.getPropertyAsList(contentModel,
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + XK_COPYPAGECONFIGURATION);
            final Resource res = slingHttpServletRequest.getResource();
            final PageManager originalPage = slingHttpServletRequest.getResourceResolver().adaptTo(PageManager.class);
            for (final String configuration : configurations) {
                //Position 0 indicates if the resource to copy is a node or a property
                //Position 1 has the page resource type
                //Position 2 has the property or node name
                final String[] values = configuration.split(MetLifeConstants.DOUBLE_COLON);
                if (values.length == MetLifeConstants.THREE) {
                    final Resource referencedPage = ContextProcessorUtils.findReferencedParentPage(
                            slingHttpServletRequest, values[1]);
                    if (referencedPage != null) {
                        if (values[0].equals(NODE)) {
                            final Resource child = referencedPage.getChild(values[2]);
                            if (child != null) {
                                final Resource childInOriginalPage = res.getChild(child.getName());
                                if (childInOriginalPage != null) {
                                    originalPage.delete(childInOriginalPage, false);
                                }
                                originalPage.copy(child, res.getPath() + Constants.SLASH
                                        + child.getName(), null, false, false);
                            } else {
                                logger.error(configuration + Constants.SPACE
                                        + NOSUCHELEMENTMESSAGE);
                            }
                        } else {
                            if (values[0].equals(PROPERTY)) {
                                final ValueMap referencedPageValueMap = referencedPage.adaptTo(ValueMap.class);
                                if (referencedPageValueMap.containsKey(values[2])) {
                                    Object referencedPropertyValue = referencedPageValueMap.get(values[2]);
                                    if (referencedPropertyValue instanceof String[]) {
                                        referencedPropertyValue = JSONUtils.jsonStringObjectListToMapList(
                                                Arrays.asList((String[]) referencedPageValueMap.get(values[2])));
                                    }
                                    contentModel.set(Constants.PAGE + Constants.DOT + values[2],
                                            referencedPropertyValue);
                                } else {
                                    logger.error(configuration + Constants.SPACE
                                            + NOSUCHELEMENTMESSAGE);
                                }
                            } else {
                                throw new IllegalArgumentException(configuration + Constants.SPACE
                                        + ILLEGALFORMATMESSAGE);
                            }
                        }
                    } else {
                        throw new NoSuchElementException(values[1] + Constants.SPACE
                                + NOSUCHELEMENTMESSAGE);
                    }
                } else {
                    throw new IllegalArgumentException(configuration + Constants.SPACE
                            + ILLEGALFORMATMESSAGE);
                }
            }
        }

    }
}
