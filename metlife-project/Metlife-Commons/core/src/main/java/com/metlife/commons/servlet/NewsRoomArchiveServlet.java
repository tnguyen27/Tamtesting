package com.metlife.commons.servlet;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * News Room Archived Servlet.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2016/02/01 | JMorataya     | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
@Properties({
        @Property(name = "service.description", value = "MetLife News Room " + "Archive Servlet"),
        @Property(name = "sling.servlet.selectors", value = "archive"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes",
        value = {"metlifeCommons/components/section/press-room-archive"})
        })
public class NewsRoomArchiveServlet extends SlingSafeMethodsServlet {


    /**
     * NEWS.
     */
    private static final String NEWS = "news";
    /**
     * LIST_PAGES_PROPERTY.
     */
    private static final String LIST_PAGES_PROPERTY = "list.pages";
    /**
     * MONTH.
     */
    private static final String MONTH = "month";
    /**
     * YEAR.
     */
    private static final String YEAR = "year";

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
            SlingHttpServletResponse response)
            throws IOException {


        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);

        if (contentModel.has(LIST_PAGES_PROPERTY)) {

            final ArrayList<Object> resp = (ArrayList<Object>) contentModel.get(
                    LIST_PAGES_PROPERTY);
            for (final Object articleObject : resp) {
                final HashMap article = (HashMap) articleObject;
                if (article.containsKey(Constants.PATH)) {
                    final String path = (String) article.get(Constants.PATH);
                    final Resource articleResource = request.getResourceResolver().getResource(path);
                    final Resource monthPage = articleResource.getParent();
                    ((HashMap) articleObject).put(MONTH, monthPage.getName());
                    final Resource yearPage = monthPage.getParent();
                    ((HashMap) articleObject).put(YEAR, yearPage.getName());
                }
            }

            final Map<String, Object> wrapper = Maps.newHashMap();
            wrapper.put(NEWS, resp);
            wrapper.put(MetLifeConstants.RESULTS, resp.size());

            //set application/json as the response content type
            response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

            //set UTF-8 as the character encoding
            response.setCharacterEncoding(MetLifeConstants.UTF8_CHARSET);

            response.getWriter().write(new Gson().toJson(wrapper));
        }
    }
}
