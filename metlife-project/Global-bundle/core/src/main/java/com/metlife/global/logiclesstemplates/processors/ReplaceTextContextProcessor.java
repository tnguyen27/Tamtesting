package com.metlife.global.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Set;

/**
 * ReplaceTextContextProcessor.
 *
 * Replace text from xk_property to display in page.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 06/04/2015 | Lesly Quiñonez  | Initial Creation
 * 2.0     | 09/06/2016 | Sergio Torres   | Adapt to MetLife Project
 * --------------------------------------------------------------------------------------
 */
@Component
@Service
public class ReplaceTextContextProcessor
        extends AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * XK_REPLACETEXT.
     */
    public static final String XK_REPLACETEXT = "xk_replaceTexts";
    /**
     * SUFFIX.
     */
    public static final String SUFFIX = "Replaced";

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY;
    }

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_REPLACETEXT);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList(XK_REPLACETEXT);
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel)
            throws Exception {
        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + XK_REPLACETEXT)) {
            final Collection<String> replacements = Utils.getPropertyAsList(contentModel,
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + XK_REPLACETEXT);
            for (final String replacement : replacements) {
                final String[] replacementAttrs = replacement.split(MetLifeConstants.DOUBLE_COLON);
                if (replacementAttrs.length == MetLifeConstants.THREE) {
                    final String propertyName = replacementAttrs[0];
                    if (contentModel.has(propertyName)) {
                        final String target = getPropertyValue(replacementAttrs[1], contentModel);
                        final String replacementAttr = getPropertyValue(replacementAttrs[2], contentModel);
                        final String propertyValue = contentModel.getAsString(propertyName);
                        contentModel.set(propertyName + SUFFIX, propertyValue.replaceAll(target, replacementAttr));
                    }
                }
            }
        }
    }

    /**
     * Get property values.
     * @param propertyName String
     * @param contentModel ContentModel
     * @return String values.
     */
    public String getPropertyValue(final String propertyName, final ContentModel contentModel) {
        String value = propertyName;
        if (contentModel.has(propertyName)) {
            value = contentModel.getAsString(propertyName);
        }
        return value;
    }
}

