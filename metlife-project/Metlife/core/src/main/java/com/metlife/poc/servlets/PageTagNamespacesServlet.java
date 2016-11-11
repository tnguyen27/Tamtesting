package com.metlife.poc.servlets;

import com.google.gson.Gson;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Tag Namespaces Servlet.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/02/05 | Jorge Hernandez       | Initial Creation
 * 1.1     | 2016/02/10 | J. Alejandro Morataya | Change list for a String
 *
 * ----------------------------------------------------------------------------
 */
@Component
@Service
@Properties({
        @Property(name = "service.description", value = "Tag Namespaces"),
        @Property(name = "sling.servlet.selectors", value = "namespaces"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes",
                value = {PageTagNamespacesServlet
                        .NEWSROOM_ARTICLE_PAGE_RESOURCE_TYPE})
})
public class PageTagNamespacesServlet extends SlingSafeMethodsServlet {
    /**
     * NEWSROOM_ARTICLE_PAGE_RESOURCE_TYPE.
     */
    public static final String NEWSROOM_ARTICLE_PAGE_RESOURCE_TYPE =
            "metlifeCommons/components/page/press-room-article";
    /**
     * NAMESPACES_KEY.
     */
    public static final String NAMESPACES_KEY = "global.newsroomNamespaces";

    /**
     * Logger.
     */
    private final Logger lOGGER = LoggerFactory.getLogger(
            PageTagNamespacesServlet.class);

    @Override
    @SuppressWarnings("unchecked")
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {
        lOGGER.info("> Default Namespaces");
        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(
                      TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);
        final ArrayList<Object> results = new ArrayList();
        results.add(contentModel.get(NAMESPACES_KEY));


        response.setCharacterEncoding(MetLifeConstants.UTF8_CHARSET);
        response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);
        response.getWriter().write(new Gson().toJson(results));

    }

}

