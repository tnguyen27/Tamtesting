package com.metlife.poc.servlets;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.servlets.AbstractExtJSONServlet;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**
 * Created with IntelliJ IDEA.
 * User: palecio
 * Date: 2014/12/10
 * Time: 9:48 PM
 */
@SlingServlet(
        label = "Options Provider Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        resourceTypes = {OptionsProviderServlet
                .XUMAKBASE_SECTION_RESOURCE_TYPE},
        selectors = {OptionsProviderServlet.OPTIONS},
        extensions = {MetLifeConstants.JSON})
public class OptionsProviderServlet
        extends AbstractExtJSONServlet {

    /**
     * XUMAKBASE_SECTION_RESOURCE_TYPE.
     */
    public static final String XUMAKBASE_SECTION_RESOURCE_TYPE =
            "xumakbase/components/section/base";
    /**
     * OPTIONS.
     */
    public static final String OPTIONS = "options";
    /**
     * OPTIONS_PROVIDER_CONFIG_PN.
     */
    private static final String OPTIONS_PROVIDER_CONFIG_PN =
            "optionsProviderPropertyName";

    @Override
    @SuppressWarnings("unchecked")
    protected final void loadElements(
            final SlingHttpServletRequest request) throws Exception {
        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                      TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);
        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                + OPTIONS_PROVIDER_CONFIG_PN)) {
            final String contextName = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + OPTIONS_PROVIDER_CONFIG_PN);
            if (contentModel.has(contextName)) {
                if (contentModel.is(contextName, List.class)) {
                    final List<String> options = (List<String>)
                            contentModel.get(contextName);
                    for (String option : options) {
                        addElement(option);

                    }
                }

            }
        }

    }

}
