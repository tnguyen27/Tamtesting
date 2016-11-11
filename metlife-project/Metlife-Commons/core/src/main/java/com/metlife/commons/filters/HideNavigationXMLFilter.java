package com.metlife.commons.filters;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;
import com.xumak.base.Constants;

/**
 * Created by javier on 8/30/16.
 */
public class HideNavigationXMLFilter implements Filter<Page> {

    /** . */
    public static final String HIDE_NAVIGATION_PROPERTY_NAME = "hideInNav";
    /** . */
    public static final String HIDE_XML_PROPERTY_NAME = "hideInXML";
    /**
     * .
     * @param page .
     * @return .
     */
    public boolean includes(final Page page) {
        return null != page.getProperties()
                && !(Constants.TRUE.equals(page.getProperties().get(HIDE_NAVIGATION_PROPERTY_NAME, Constants.BLANK))
                 || Constants.TRUE.equals(page.getProperties().get(HIDE_XML_PROPERTY_NAME, Constants.BLANK)));
    }

}
