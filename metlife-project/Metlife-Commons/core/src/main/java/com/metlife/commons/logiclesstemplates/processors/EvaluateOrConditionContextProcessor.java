package com.metlife.commons.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.xumak.base.Constants;

/**
 * EvaluateOrConditionContextProcessor
 * This context processor obtains the list of properties stored in the <b>xk_evaluateOrCondition</b> or
 * any property that contains <xk_evaluateOrCondition</b> and makes an OR condition with their values.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 26/06/2015 | Lesly Quiñonez  | Initial Creation
 * 1.1     | 05/07/2016 | Roger Jimenez   | evaluate 2 conditions
 * --------------------------------------------------------------------------------------
 */

@Component
@Service
public class EvaluateOrConditionContextProcessor
        extends AbstractConfigurationContextProcessor {

    private static final String XK_EVALUATE_OR_CONDITIONS = "xk_evaluateOrConditions";
    private static final String XK_EVALUATE_OR_CONDITIONS1 = "xk_evaluateOrConditions1";
    private static final String UNDERSCORE = "_";
    private static final String OR = "or";
    private static final String ESCAPE_DOT = ".";
    private static final String NOT = "not";

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_EVALUATE_OR_CONDITIONS);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public boolean mustExist() {
        return false;
    }

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY - 1;
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel)
            throws Exception {
        final Map<String, Object> configObject = (Map<String, Object>)
                contentModel.get(Constants.CONFIG_PROPERTIES_KEY);
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        final boolean condition1 = configObject.containsKey(XK_EVALUATE_OR_CONDITIONS);
        final boolean condition2 = configObject.containsKey(XK_EVALUATE_OR_CONDITIONS1);
        if (condition1 || condition2) {
            final Iterator<String> keys = configObject.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                if (key.contains(XK_EVALUATE_OR_CONDITIONS) || key.contains(XK_EVALUATE_OR_CONDITIONS1)) {
                    final Collection<String> props = (Collection<String>) configObject.get(key);
                    final Iterator<String> propsIterator = props.iterator();
                    boolean evaluation = Boolean.FALSE;
                    final StringBuffer propertyName = new StringBuffer(OR);
                    while (propsIterator.hasNext()) {
                        final String propName = propsIterator.next();
                        propertyName.append(UNDERSCORE).append(propName.split(Pattern.quote(ESCAPE_DOT))[1]);
                        evaluation = evaluation || getValue(propName, contentModel);
                    }

                    contentObject.put(propertyName.toString(), evaluation);
                }
            }
        }
    }

    /**
     * Evaluate the property value.
     * @param propName Property to be evaluated
     * @param contentModel Content Model
     * @return returns TRUE if the property exists AND has Content
     * (if the content is boolean gets this value, otherwise returns TRUE).
     * @throws Exception if failed
     */
    public static boolean getValue(final String propName, final ContentModel contentModel) throws Exception {
        boolean value = Boolean.FALSE;
        boolean negate = Boolean.FALSE;
        String property = propName;
        if (propName.startsWith(NOT)) {
            negate = Boolean.TRUE;
            property = propName.replace(NOT, Constants.BLANK);
        }
        if (contentModel.has(property)) {
            final String propValue = contentModel.getAsString(property);
            if (StringUtils.isNotEmpty(propValue)) {
                if (contentModel.is(property, Boolean.class)) {
                    value = contentModel.getAs(property, Boolean.class);
                } else {
                    value = Boolean.TRUE;
                }
            } else {
                value = Boolean.FALSE;
            }
        } else {
            value = Boolean.FALSE;
        }
        if (negate) {
            value = !value;
        }
        return  value;
    }


}
