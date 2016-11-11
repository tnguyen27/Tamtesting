package com.metlife.commons.services;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Session;
import java.util.Date;


/**
 * --------------------------------------------------------------------------------------
 * CacheInvalidationService
 * --------------------------------------------------------------------------------------
 * Provides methods to invalidate dispatcher and akamai cache.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 14/12/2015 | Edwin Burrion | Initial Creation
 * --------------------------------------------------------------------------------------
 */


public interface CacheInvalidationService {

    /**
    * Gets the value of the property configPagesPath set as an OSGI property.
    *
    * @return a <code>String</code> with the configPagesPath value set on OSGI or the default path
    * /content/configuracion in empty case.
    */
    String getConfigurationPagesPath();



    /**
    * Collects and publishes all pages containing a reference of the content configuration page activated.
    *
    * @param activatedPagePath <code>String</code> the activated page path
    * @param modificationDate <code>Date</code> modificacion date of the page
    * @param resourceResolver <code>ResourceResolver</code> object to geth the page resources
    * @param adminSession <code>Session</code> object with admin privileges in order to publish resources
    */
    void collectAndPublishReferences(final String activatedPagePath, final Date modificationDate,
            final ResourceResolver resourceResolver, final Session adminSession);


    /**
    * Checks if the activated page is the filter set in purgeByTemplate OSGI property and calls a couple method
    * cleaning the dispatcher cache and purging the akamai cache.
    *
    * @param path <code>String</code> the activated page path
    * @param resourceResolver <code>ResourceResolver</code> object to get a page object
    */
    void checkTemplate(final String path, final ResourceResolver resourceResolver);


    /**
    * Checks if the given page is in templates list configured in OSGI console, if it does, gets all components
    * of this page as component-path/selector map.
    *
    * @param templatePath <code>String</code> the page template
    * @param page <code>Page</code> either the current activated page or a page requested to be purged by the servlet
    *             'PurgeAkamaiServlet'.
    * @param resourceResolver <code>ResourceResolver</code> object to resolve resources
    */
    void purgeJsonByTemplateAndComponents(final String templatePath, final Page page,
            final ResourceResolver resourceResolver);
}
