package com.metlife.commons.servlet;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import com.xumak.base.Constants;

import java.io.IOException;

/**.


 * FormsLibraryServlet
 *
 * Receives a list of topics and get the format of list for select option
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer                 | Changes
 * 1.0     | 2016/09/15 | J. Alejandro Morataya     | Initial creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
@Properties({
        @Property(name = "service.description", value = "MetLife Generic List Servlet"),
        @Property(name = "sling.servlet.selectors", value = "topics"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes",
        value = {"metlifeCommons/components/section/press-article"})
        })

public class GenericListServlet  extends SlingSafeMethodsServlet {

    private static final int PRESS_ROOM_POS = 5;
    private static final String NEWS_ROOM_COMPONENT_PATH = "jcr:content/content-parsys/press_room_archive";
    private static final String TOPICS_PROPERTY = "topics";

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String pressRoomParentPath = getPressRoomParentPath(request.getResource().getPath());
        final Resource parentResource  =  resourceResolver.getResource(pressRoomParentPath);
        final String topicList = getTopicList(parentResource);

        final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        final Page listPage = pageManager.getPage(topicList);
        if (listPage != null) {
            final GenericList list = listPage.adaptTo(GenericList.class);
            response.getWriter().write(new Gson().toJson(list.getItems()));

        } else {
            response.getWriter().write(new Gson().toJson("empty"));

        }

    }

    /**
     * Get the Path of the Parent path that is a PressRoom Page
     * This functions need the Press article pages has the following structure
     * PressRoom/Year/month/Article.
     * @param path path of the current page.
     * @return path of the PressRoom Page.
     */
    private String getPressRoomParentPath(final String path) {

        final String newString = StringUtils.substring(path, 0,
                StringUtils.lastOrdinalIndexOf(path, Constants.SLASH, PRESS_ROOM_POS));
        newString.length();
        return newString;


    }

    /**
     * get topic list from Press Room.
     * @param parentResource page Resource.
     * @return string with the path of the topic.
     */
    private String getTopicList(final Resource parentResource) {

        String topicList = "";
        if (parentResource != null) {
            final Resource pressRoomResource = parentResource.getChild(NEWS_ROOM_COMPONENT_PATH);
            if (pressRoomResource != null) {

                final ValueMap valueMapPresRoom = pressRoomResource.adaptTo(ValueMap.class);
                if (valueMapPresRoom != null) {
                    topicList = (String) valueMapPresRoom.get(TOPICS_PROPERTY);

                }
            }

        }
        return topicList;

    }

}
