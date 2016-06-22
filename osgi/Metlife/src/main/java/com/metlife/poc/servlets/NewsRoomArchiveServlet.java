package com.metlife.poc.servlets;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;

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
        @Property(name = "service.description", value = "MetLife News Room "
                + "Archive Servlet"),
        @Property(name = "sling.servlet.selectors", value = "archive"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes", value =
                {"MetlifeApp/components/section/news-room-archive"})


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
