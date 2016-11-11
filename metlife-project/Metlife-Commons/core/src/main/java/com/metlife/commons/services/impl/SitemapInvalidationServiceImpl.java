package com.metlife.commons.services.impl;

import com.metlife.commons.services.SitemapInvalidationService;
import com.metlife.commons.util.DispatcherFlush;
import com.xumak.aem.akamai.ccu.CcuManager;
import com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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
@Component(
        label = "Akamai - Sitemap Invalidation Service",
        description = "Service that invaliudates Akamai and Dispatcher sitemap",
        metatype = true, immediate = true)
@Service(value = SitemapInvalidationService.class)
public class SitemapInvalidationServiceImpl implements SitemapInvalidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapInvalidationServiceImpl.class);

    private static final String SITEMAP_NAME = "/sitemap.xml";

    @Reference
    private FlushAkamaiItemsJob flushAkamaiItemsJob;

    @Reference
    private CcuManager ccuManager;

    /**
     * Method that ivalidates the sitemap on the dispatched and purge the Akamai cache.
     *
     * @param path the path to be processed
     * @param resourceResolver the resource resolver
     * */
    public void purgeByPath(final String path, final ResourceResolver resourceResolver) {
        LOGGER.info("++++ Starting Sitemap invalidations.... ");
        LOGGER.info("processing path: " + path);

        // step 1: locating the domain acording to the path in the configuration
        final List<String> domainList = locateDomain(path);

        // step 2: forming sitemap xml for every domain
        for (final String domain : domainList) {
            final String sitemapPath = domain + path + SITEMAP_NAME;

            LOGGER.info("----SITEMAP.XML path: " + sitemapPath);
            // step 3: flushing dispatcher
            flushDispatcher(sitemapPath);

            // step 4: purging akamai cache
            purgeAkamai(sitemapPath);
        }


    }

    /**
     * Method in charge of flush the sitemap on the dispatcher.
     *
     * @param path the path to the sitemap
     * */
    private void flushDispatcher(final String path) {
        final String newPath = flushAkamaiItemsJob.rewriteInternalLink(path);
        for (final Object dispatcher : flushAkamaiItemsJob.dispatchersToList()) {
            LOGGER.debug("+++ Dispatcher, json to flush: " + newPath);
            final DispatcherFlush dispatcherFlush = new DispatcherFlush((String) dispatcher, newPath);
            final String response = dispatcherFlush.flush();
            LOGGER.debug("+++ Dispatcher response: " + response);
        }
    }

    /**
     * Method in charge to purge the sitemap on Akamai.
     *
     * @param path the path to the sitemap
     * */
    private void purgeAkamai(final String path) {

        final com.xumak.aem.akamai.ccu.PurgeResponse response;

        final String newPath = flushAkamaiItemsJob.rewriteInternalLink(path);
        LOGGER.info("Purge Akamai sitemap for " + newPath);
        final Set<String> set = new HashSet<String>(Arrays.asList(newPath));
        try {
            response = ccuManager.purgeByUrls(set);
            LOGGER.info("respoonse"  + response);
        } catch (Exception e) {
            LOGGER.error("Forbidden URL = " + path);
        }
    }


    /**
     *
     * Return the configured domain list for the given path.
     *
     * @param pathToValidate the path to validate
     *
     *
     * @return the domain list
     * */
    private List<String> locateDomain(final String pathToValidate) {

        final Hashtable<String, List<String>> mappedPathDomains = flushAkamaiItemsJob.getMappedDomains();
        List<String> results = null;
        for (final String path : mappedPathDomains.keySet()) {
            if (pathToValidate.startsWith(path)) {
                results = mappedPathDomains.get(path);
                break;
            }
        }
        return results;
    }

}
