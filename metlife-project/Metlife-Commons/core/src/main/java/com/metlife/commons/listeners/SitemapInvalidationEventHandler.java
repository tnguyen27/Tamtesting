package com.metlife.commons.listeners;

import com.day.cq.replication.ReplicationAction;
import com.metlife.commons.services.SitemapInvalidationService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


/**
 * --------------------------------------------------------------------------------------
 * SitemapInvalidationEventHandler
 * --------------------------------------------------------------------------------------
 *
 * Listen to a repository replication event in order to invalidate the sitemap on dispatcher and akamai.
 *
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 02/08/2016 | Rainman Sian  | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component(
        label = "Akamai - Sitemap Invalidation Event Handler",
        description = "Listen for a replication event in order to invalidate the sitemap on dispatcher an Akamai",
        immediate = true,
        metatype = true)
@Service(value = {SitemapInvalidationEventHandler.class, EventHandler.class})
@Properties({
        @Property(name = EventConstants.EVENT_TOPIC,
        value = {ReplicationAction.EVENT_TOPIC},
        label = "Event topic")})
public class SitemapInvalidationEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapInvalidationEventHandler.class);

    @Reference
    private SitemapInvalidationService sitemapInvalidationService;
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private ResourceResolver resourceResolver;

    @Override
    public void handleEvent(final Event event) {

        LOGGER.info("++++ Starting Sitemap Invalidation Handler....");

        final List<String> paths = Arrays.asList((String[]) event.getProperty(ReplicationAction.PROPERTY_PATHS));

        try {
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            for (final String activatedPagePath : paths) {
                sitemapInvalidationService.purgeByPath(activatedPagePath, resourceResolver);
            }
        } catch (LoginException le) {
            LOGGER.error("error on handleEvent", le);
        } finally {
            closeResources();
        }
    }

    /**
     * Closed resources.
     * */
    private void closeResources() {
        if (null != resourceResolver && resourceResolver.isLive()) {
            resourceResolver.close();
        }
    }
}
