package com.metlife.poc.servlets;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**
 * Search Results Servlet.
 * Example request:
 * /page/path/jcr:content/content-parsys/search_results.results
 * .jsonquery=insurance&start=1&limit=2
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2016/02/04 | Palecio       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@SlingServlet(
        label = "Search Results Servlet",
        metatype = true,
        methods = {MetLifeConstants.HTTP_GET},
        resourceTypes = {SearchResultsServlet.SEARCH_RESULTS_RESOURCE_TYPE,
                SearchResultsServlet.FORM_LIBRARY_RESOURCE_TYPE},
        selectors = {MetLifeConstants.RESULTS},
        extensions = {MetLifeConstants.JSON})
@Properties({
        @Property(name = "service.description", value = "MetLife Search "
                + "Results Servlet")
})
public class SearchResultsServlet extends SlingAllMethodsServlet {

    /**
     * Logger.
     */
    private final Logger lOG = LoggerFactory.getLogger(SearchResultsServlet
            .class);
    /**
     * SEARCH_RESULTS_RESOURCE_TYPE.
     */
    public static final String SEARCH_RESULTS_RESOURCE_TYPE =
            "MetlifeApp/components/section/search-results";
    /**
     * FORM_LIBRARY_RESOURCE_TYPE.
     */
    public static final String FORM_LIBRARY_RESOURCE_TYPE =
            "MetlifeApp/components/section/form-library";

    @SuppressWarnings("unchecked")
    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {
        lOG.info("search results servlet");

        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                      TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);

        if (contentModel.has(Constants.CONTENT + Constants.DOT
                + MetLifeConstants.RESULTS)) {
            final Map<String, Object> resp = (Map<String, Object>)
                    contentModel.get(Constants.CONTENT + Constants.DOT
                            + MetLifeConstants.RESULTS);
            final Map<String, Object> wrapper = Maps.newHashMap();
            wrapper.put(MetLifeConstants.RESPONSE, resp);

            //set application/json as the response content type
            response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

            //set UTF-8 as the character encoding
            response.setCharacterEncoding(MetLifeConstants.UTF8_CHARSET);

            response.getWriter().write(new Gson().toJson(wrapper));
        }
    }
}
