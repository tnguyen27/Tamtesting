package com.metlife.commons.services;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * --------------------------------------------------------------------------------------
 * SitemapInvalidationService
 * --------------------------------------------------------------------------------------
 * Provides methods to invalidate dispatcher and akamai cache.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 05/09/2016 | Rainman Sian  | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public interface SitemapInvalidationService {

    /**
     * Method that ivalidates the sitemap on the dispatched and purge the Akamai cache.
     *
     * @param path the path to be processed
     * @param resourceResolver the resource resolver
     * */
    void purgeByPath(final String path, final ResourceResolver resourceResolver);
}
