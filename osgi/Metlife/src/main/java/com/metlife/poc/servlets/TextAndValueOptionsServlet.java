package com.metlife.poc.servlets;

import com.google.gson.Gson;
import com.metlife.poc.util.JSONUtils;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**
 * Created by arevolorio on 2/16/16.
 * ----------------------------------------------------------------------------
 *
 * CHANGE HISTORY
 * ----------------------------------------------------------------------------
 * Version  | Date          | Developer          | Changes
 * 1.0      | 2/16/16       | Allan Revolorio    | Initial Creation
 * ----------------------------------------------------------------------------
 */
@SlingServlet(
        label = "Text and Value Options Provider Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        resourceTypes = {TextAndValueOptionsServlet
                .XUMAKBASE_SECTION_RESOURCE_TYPE},
        selectors = {TextAndValueOptionsServlet.TEXT_VALUE_OPTIONS},
        extensions = {MetLifeConstants.JSON})
public class TextAndValueOptionsServlet extends SlingAllMethodsServlet {


    /**
     * TEXT_VALUE_OPTIONS.
     */
    public static final String TEXT_VALUE_OPTIONS = "textValueOptions";
    /**
     * TEXT_OPTIONS_KEY.
     */
    public static final String TEXT_OPTIONS_KEY = "textOptionsKey";
    /**
     * VALUE_OPTIONS_KEY.
     */
    public static final String VALUE_OPTIONS_KEY = "valueOptionKey";
    /**
     * OPTIONS_LIST_PROPERTY.
     */
    public static final String OPTIONS_LIST_PROPERTY = "optionListProperty";
    /**
     * XUMAKBASE_SECTION_RESOURCE_TYPE.
     */
    public static final String XUMAKBASE_SECTION_RESOURCE_TYPE =
            "xumakbase/components/section/base";

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws IOException {
        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                      TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);
        if ((contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                + OPTIONS_LIST_PROPERTY))
                && (contentModel.has(Constants.CONFIG_PROPERTIES_KEY
                + Constants.DOT
                + TEXT_OPTIONS_KEY))
                && (contentModel.has(Constants.CONFIG_PROPERTIES_KEY
                + Constants.DOT
                + VALUE_OPTIONS_KEY))) {
            final String textKeyName = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + TEXT_OPTIONS_KEY);
            final  String valueKeyName = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + VALUE_OPTIONS_KEY);
            final String optionListProperty = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + OPTIONS_LIST_PROPERTY);
            if (contentModel.is(optionListProperty, List.class)) {
                final List<String> optionStringList = (List) contentModel.get(
                        optionListProperty);
                if (!optionStringList.isEmpty()) {
                    final List<Map> optionsMapList = JSONUtils
                            .jsonStringObjectToMapList(optionStringList);
                    final List<Map> optionsList = getOptionsList(optionsMapList,
                            textKeyName, valueKeyName);
                    response.getWriter().write(new Gson().toJson(optionsList));
                }
            }
        }
    }


    /**
     * Get options list.
     * @param optionsMaps List<Map>.
     * @param textkey String.
     * @param valueKey String.
     * @return List<Map>.
     */
    private List<Map> getOptionsList(final List<Map> optionsMaps,
                                     final String textkey, final String
                                             valueKey) {
        final List<Map> optionList = new ArrayList<>();
        if ((StringUtils.isNotEmpty(textkey)) && (StringUtils.isNotEmpty(
                valueKey))) {
            for (Map optionMap : optionsMaps) {
                if ((optionMap.containsKey(textkey)) && (optionMap
                        .containsKey(valueKey))) {
                    if ((optionMap.get(textkey) instanceof String)
                            && (optionMap.get(valueKey) instanceof String)) {
                        final Map<String, Object> newOption = new HashMap<>();
                        final String text = (String) optionMap.get(textkey);
                        final String value = (String) optionMap.get(valueKey);
                        newOption.put(Constants.TEXT, text);
                        newOption.put(MetLifeConstants.VALUE, value);
                        optionList.add(newOption);
                    }
                }
            }
        }
        return optionList;
    }

}
