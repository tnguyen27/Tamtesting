package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * EvaluateOrConditionContextProcessor
 * This context processor obtains the list of properties stored in the
 * <b>xk_evaluateOrCondition</b> or
 * any property that contains <xk_evaluateOrCondition</b> and makes an OR
 * condition with their values.
 * The criteria to evaluate the property value is the following
 * * Exists?
 * * Has content?
 * * Is it a boolean value? otherwise it's TRUE because it has content.
 *
 * WARNING. If you want to have more than one evaluation you should to have
 * the property
 * <b>xk_evaluateOrCondition</b>.
 * Take care with the logical operator precedence.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 26/06/2015 | Lesly Quiñonez  | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class EvaluateOrConditionContextProcessor
        extends AbstractConfigurationContextProcessor {

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY - 1;
    }

    /**
     * Get required property names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(MetLifeConstants.XK_EVALUATE_CONDITIONS);
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
        final Map<String, Object> configObject = (Map<String, Object>)
                contentModel
                .get(Constants.CONFIG_PROPERTIES_KEY);
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        if (configObject.containsKey(
                MetLifeConstants.XK_EVALUATE_OR_CONDITIONS)) {
            final Iterator<String> keys = configObject.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                if (key.contains(MetLifeConstants.XK_EVALUATE_OR_CONDITIONS)) {
                    final Collection<String> props = (Collection<String>)
                            configObject.get(key);
                    final Iterator<String> propsIterator = props.iterator();
                    boolean evaluation = Boolean.FALSE;
                    String propertyName = MetLifeConstants.OR;
                    while (propsIterator.hasNext()) {
                        final  String propName = propsIterator.next();
                        propertyName += MetLifeConstants.UNDERSCORE
                                + propName.split(
                                MetLifeConstants.ESCAPE_DOT)[1];
                        evaluation = evaluation || Utils.getValue(propName,
                                contentModel);
                    }

                    contentObject.put(propertyName, evaluation);
                }
            }
        }
    }
}
