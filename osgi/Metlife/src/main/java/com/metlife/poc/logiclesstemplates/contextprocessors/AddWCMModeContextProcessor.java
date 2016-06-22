package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.wcm.api.WCMMode;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import com.xumak.base.Constants;

/**.
 * AddWCMModeContextProcessor
 * This context processor is intended to add the real WCMMode of the current
 * request
 * since components being referenced with 'Reference' store the WCMMode from
 * the containing page
 * *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/02/21 | Jorge Hernandez       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddWCMModeContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {
    /**
     * BASE_FIELD_RESOURCE_TYPE.
     */
    public static final String BASE_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/base";
    /**
     * WCMMODE_KEY.
     */
    public static final String WCMMODE_KEY = "wcmmode";
    /**
     * WCMMODE_IS_EDIT_OR_DESIGN_KEY.
     */
    public static final String WCMMODE_IS_EDIT_OR_DESIGN_KEY =
            "isEditOrDesignMode";
    /**
     * WCMMODE_IS_DISABLED_KEY.
     */
    public static final String WCMMODE_IS_DISABLED_KEY = "isDisabled";

    @Override
    public final String requiredResourceType() {
        return BASE_FIELD_RESOURCE_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {
        final WCMMode wcmMode = WCMMode.fromRequest(request);
        if (wcmMode == WCMMode.DESIGN || wcmMode == WCMMode.EDIT) {
            contentModel.set(WCMMODE_KEY + Constants.DOT
                    + WCMMODE_IS_EDIT_OR_DESIGN_KEY, Constants.TRUE);
        } else if (wcmMode == WCMMode.DISABLED) {
            contentModel.set(WCMMODE_KEY + Constants.DOT
                    + WCMMODE_IS_DISABLED_KEY, Constants.TRUE);
        }
    }

}
