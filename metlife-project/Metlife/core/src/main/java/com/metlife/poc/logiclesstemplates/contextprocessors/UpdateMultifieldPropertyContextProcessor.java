package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * UpdateMultifieldPropertyContextProcessor
 *
 * Updates the values of a property in a multifield. Use the property
 * 'xk_updatedMultifieldProperty'
 * to specify which property should be updated and the new values that should
 * be used. The format
 * of this property should be the following:
 *
 * xk_updatedMultifieldProperty:
 * [src_context].[src_array_name]>[dst_context].[dst_multifield_name]
 * .[dst_multifield_property_name]
 *
 * Example:
 * xk_updatedMultifieldProperty:
 * global.newIcons>global.iconsMultifield.icon
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 30/12/2014 | Gabriel Orozco  | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class UpdateMultifieldPropertyContextProcessor
        extends AbstractConfigurationContextProcessor {

    /**
     * UPDATED_MULTIFIELD_PROPERTY.
     */
    public static final String UPDATED_MULTIFIELD_PROPERTY =
            "xk_updatedMultifieldProperty";
    /**
     * GREATER_THAN.
     */
    public static final String GREATER_THAN = ">";
    /**
     * DOT_CHAR.
     */
    public static final char DOT_CHAR = '.';


    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 2; //must be executed after
        // AddSpooledImagePathContextProcessor
    }

    @Override
    protected final boolean mustExist() {
        return false;
    }

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(UPDATED_MULTIFIELD_PROPERTY);
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel)
            throws Exception {
        final List<String> updates = (List<String>) Utils.getPropertyAsList(
                contentModel,
                Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                        + UPDATED_MULTIFIELD_PROPERTY);
        if (CollectionUtils.isNotEmpty(updates)) {
            for (String update : updates) {
                if (validUpdateFormat(update)) {
                    final String src = StringUtils.deleteWhitespace(
                            update.split(GREATER_THAN)[0]);
                    final String dst = StringUtils.deleteWhitespace(
                            update.split(GREATER_THAN)[1]);
                    if (validSourceAndDestination(src, dst)) {
                        final int dstLastDot = dst.lastIndexOf(Constants.DOT);
                        final String dstMultifield = dst.substring(0,
                                dstLastDot);
                        final String dstPropertyName = dst.substring(
                                dstLastDot + 1);
                        if (validMultifieldAndPropertyName(dstMultifield,
                                dstPropertyName)) {
                            List srcProperties;
                            List<Map> dstMaps;
                            try {
                                srcProperties = (List) contentModel.get(src);
                                dstMaps = (List<Map>) contentModel.get(
                                        dstMultifield);
                            } catch (Exception e) {
                                srcProperties = null;
                                dstMaps = null;
                            }
                            if (validSourceAndDestinationProperties(
                                    srcProperties, dstMaps)) {
                                for (int i = 0; i < srcProperties.size(); i++) {
                                    final  Object property =
                                            srcProperties.get(i);
                                    final Map map = dstMaps.get(i);
                                    map.put(dstPropertyName, property);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    /**
     * Valid update format.
     * @param update String.
     * @return Boolean.
     */
    private boolean validUpdateFormat(final String update) {
        boolean valid = false;
        if (StringUtils.isNotBlank(update)
                && update.contains(GREATER_THAN)) {
            valid = true;
        }
        return valid;
    }

    /**
     * Valid source and destination.
     * @param src String.
     * @param dst String.
     * @return Boolean.
     */
    private boolean validSourceAndDestination(final String src,
                                              final String dst) {
        boolean valid = false;
        if (StringUtils.isNotBlank(src)
                && StringUtils.isNotBlank(dst)
                && dst.contains(Constants.DOT)
                && dst.charAt(dst.length() - 1) != DOT_CHAR) {
            valid = true;
        }
        return valid;
    }

    /**
     * Valid multifield and property name.
     * @param dstMultifield String.
     * @param dstPropertyName String.
     * @return Boolean.
     */
    private boolean validMultifieldAndPropertyName(final String dstMultifield,
                                                   final String
                                                           dstPropertyName) {
        boolean valid = false;
        if (StringUtils.isNotBlank(dstMultifield)
                && StringUtils.isNotBlank(dstPropertyName)) {
            valid = true;
        }
        return valid;
    }

    /**
     * Valid source and destination properties.
     * @param srcProperties List<String>.
     * @param dstMaps List<Map>.
     * @return Boolean.
     */
    private boolean validSourceAndDestinationProperties(final List<String>
                                                                srcProperties,
                                                        final List<Map>
                                                                dstMaps) {
        boolean valid = false;
        if (CollectionUtils.isNotEmpty(srcProperties)
                && CollectionUtils.isNotEmpty(dstMaps)
                && srcProperties.size() <= dstMaps.size()
                ) {
            valid = true;
        }
        return valid;
    }
}
