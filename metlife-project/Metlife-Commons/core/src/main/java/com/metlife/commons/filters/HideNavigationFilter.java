package com.metlife.commons.filters;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;

import com.xumak.base.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: palecio
 * Date: 11/7/14
 * Time: 9:12 AM
 */
public class HideNavigationFilter implements Filter<Page> {
    /** . */
    public static final String HIDE_NAVIGATION_PROPERTY_NAME = "hideInNav";
    /**
     * .
     * @param page .
     * @return .
     */
    public boolean includes(final Page page) {
        return null != page.getProperties()
                && !Constants.TRUE.equals(page.getProperties().get(HIDE_NAVIGATION_PROPERTY_NAME, Constants.BLANK));
    }

}
