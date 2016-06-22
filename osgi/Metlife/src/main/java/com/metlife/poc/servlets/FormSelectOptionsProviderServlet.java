package com.metlife.poc.servlets;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.servlets.AbstractExtJSONServlet;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**
 * Created by jhernandez on 2/21/16.
 */
@SlingServlet(
        label = "Form Select Options Provider Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        resourceTypes = {FormSelectOptionsProviderServlet
                .XUMAKBASE_SECTION_RESOURCE_TYPE},
        selectors = {FormSelectOptionsProviderServlet.OPTIONS},
        extensions = {MetLifeConstants.JSON})
public class FormSelectOptionsProviderServlet extends AbstractExtJSONServlet {
    /**
     * XUMAKBASE_SECTION_RESOURCE_TYPE.
     */
    public static final String XUMAKBASE_SECTION_RESOURCE_TYPE =
            "MetlifeApp/components/forms/container";
    /**
     * OPTIONS.
     */
    public static final String OPTIONS = "observes";
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
                    final List<Map<String, String>> options =
                            (List<Map<String, String>>)
                                    contentModel.get(contextName);
                    for (Map<String, String> option : options) {
                        addElement(option.get(Constants.TEXT), option.get(
                                MetLifeConstants.VALUE));
                    }
                }

            }
        }

    }
}
