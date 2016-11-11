package com.metlife.global.microsites.util;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;

/**
 * Created by dtello on 7/7/16.
 */
public final class ContextProcessorUtils {

    /**
     * ContextProcessorUtils.
     */
    private ContextProcessorUtils() {

    }

    /**
     * Finds a parent page of the specified type.
     * @param slingHttpServletRequest http request
     * @param resourceType resource type to find
     * @return Resource representing the parent page
     */
    public static Resource findReferencedParentPage(final SlingHttpServletRequest slingHttpServletRequest,
                                                    final String resourceType) {
        Resource resourcesIterator = slingHttpServletRequest.getResource();
        Resource referencedParentPage = null;
        Iterator<Resource> childIterator = null;
        Resource child = null;
        while (resourcesIterator != null) {
            childIterator = resourcesIterator.listChildren();
            while (childIterator.hasNext()) {
                child = childIterator.next();
                if (child.getResourceType().equals(resourceType)) {
                    resourcesIterator = null;
                    referencedParentPage = child;
                    break;
                }
            }
            if (referencedParentPage != null) {
                break;
            } else {
                resourcesIterator = resourcesIterator.getParent();
            }
        }
        return referencedParentPage;
    }
}
