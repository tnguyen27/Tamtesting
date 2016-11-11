package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * ValueMultiplierContextProcessor
 *
 * Multiplies the value of a property in a numberfield. Use the property
 * 'xk_multiplyNumberfieldProperty'
 * to specify which property should be multiplied. The format
 * of this property should be the following:
 *
 * xk_multiplyNumberfieldProperty:
 * [src_context]
 *
 * Example:
 * xk_multiplyNumberfieldProperty:
 * content.numberfieldValue
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 26/01/2016 | Pablo García    | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class ValueMultiplierContextProcessor
        extends AbstractConfigurationContextProcessor {

    /**
     * MULTIPLY_NUMBERFIELD_PROPERTY.
     */
    public static final String MULTIPLY_NUMBERFIELD_PROPERTY =
            "xk_multiplyNumberfieldProperty";
    /**
     * MULTIPLY_FACTOR.
     */
    public static final String MULTIPLY_FACTOR = "xk_multiplyFactor";
    /**
     * MULTIPLIED_NUMBERFIELD_CONTEXT_KEY.
     */
    public static final String MULTIPLIED_NUMBERFIELD_CONTEXT_KEY =
            "numberMultiplied";


    @Override
    protected final boolean mustExist() {
        return false;
    }

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(MULTIPLY_NUMBERFIELD_PROPERTY, MULTIPLY_FACTOR);
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel)
            throws Exception {
        /**
         * configObject
         */
        final Map<String, Object> configObject = (Map<String, Object>)
                contentModel
                .get(Constants.CONFIG_PROPERTIES_KEY);
        /**
         * contentObject
         */
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        /**
         * valueToMultiply
         */
        final String valueToMultiply = (String) configObject.get(
                MULTIPLY_NUMBERFIELD_PROPERTY);
        /**
         * valueMultiplied
         */
        Long valueMultiplied = null;
        try {
            final Long multiplyFactor =
                    (Long) configObject.get(MULTIPLY_FACTOR);

            //Original value is in seconds, it needs to be transformed to ms
            valueMultiplied = Long.parseLong(contentModel.getAsString(
                    valueToMultiply)) * multiplyFactor;
        } catch (NumberFormatException e) {
            log.error("Error parsing values", e.getMessage());
        }

        contentObject.put(MULTIPLIED_NUMBERFIELD_CONTEXT_KEY, valueMultiplied);
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }
}
