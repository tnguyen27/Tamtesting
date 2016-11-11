package com.metlife.commons.listeners;

import com.day.cq.replication.ReplicationAction;
import com.metlife.commons.services.CacheInvalidationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * --------------------------------------------------------------------------------------
 * CacheInvalidationEventHandler
 * --------------------------------------------------------------------------------------
 * Listen to repository replication event in order to invalidate dispatcher and akamai
 * cache.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 14/12/2015 | Edwin Burrion | Initial Creation
 * 1.1     | 01/08/2016 | Rainman Sian  | Adapted to metlife
 * --------------------------------------------------------------------------------------
 */


@Component(label = "Akamai - Cache Invalidation Event Handler",
        description = "Listen to repository replication event in order to invalidate dispatcher cache",
        immediate = true, metatype = true)
@Service(value = {CacheInvalidationEventHandler.class, EventHandler.class})
@Properties({
        @Property(name = EventConstants.EVENT_TOPIC, value = {ReplicationAction.EVENT_TOPIC}, label = "Event topic")
        })

public class CacheInvalidationEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInvalidationEventHandler.class);

    private ResourceResolver resourceResolver;
    private Session adminSession;

    @Reference
    private CacheInvalidationService cacheService;
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void handleEvent(final Event event) {

        LOGGER.debug("-+-+-+-+-+ Akamai - Cache Invalidation Event Handler STARTED!!!");
        if (event.containsProperty(ReplicationAction.PROPERTY_MODIFICATION_DATE)
                && event.containsProperty(ReplicationAction.PROPERTY_PATHS)) {
            final Date modificationDate =
                    ((GregorianCalendar) event.getProperty(ReplicationAction.PROPERTY_MODIFICATION_DATE)).getTime();
            final List<String> paths = Arrays.asList((String[]) event.getProperty(ReplicationAction.PROPERTY_PATHS));

            try {
                resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
                if (null != resourceResolver && null != modificationDate && null != paths) {
                    adminSession = resourceResolver.adaptTo(Session.class);
                    if (null != adminSession) {
                        for (final String activatedPagePath : paths) {
                            if (!StringUtils.isEmpty(activatedPagePath) && activatedPagePath.toLowerCase().
                                    startsWith(cacheService.getConfigurationPagesPath())) {
                                cacheService.collectAndPublishReferences(activatedPagePath, modificationDate,
                                        resourceResolver, adminSession);
                            } else {
                                cacheService.checkTemplate(activatedPagePath, resourceResolver);
                            }
                        }
                    }
                }

                closeResources();
            } catch (LoginException ex) {
                LOGGER.error(ex.getMessage());
            } finally {
                closeResources();
            }
        }
    }

    /**
     * Closed resources.
     * */
    private void closeResources() {
        if (null != resourceResolver && resourceResolver.isLive()) {
            resourceResolver.close();
        }

        if (null != resourceResolver && adminSession.isLive()) {
            adminSession.logout();
        }
    }
}
