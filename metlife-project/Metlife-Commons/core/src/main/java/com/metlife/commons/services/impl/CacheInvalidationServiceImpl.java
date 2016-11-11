package com.metlife.commons.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.metlife.commons.services.CacheInvalidationService;
import com.metlife.commons.util.DispatcherFlush;
import com.xumak.aem.akamai.ccu.CcuManager;
import com.xumak.aem.akamai.ccu.PurgeResponse;
import com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob;
import com.xumak.base.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


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
 * 1.1     | 31/08/2016 | Rainman Sian  | Adapted to Metlife
 * --------------------------------------------------------------------------------------
 */

@Component(
        label = "Akamai - Cache Invalidation Service",
        description = "Service invalidating Akamai and Dispatcher cache",
        metatype = true, immediate = true)

@Service(value = CacheInvalidationService.class)

@Properties({

        /*@Property(
        name = CacheInvalidationServiceImpl.PN_MAPPING,
        value = {
        "/content/configuracion/variables-globales|/etc/designs/LAN",
        "/content/configuracion/variables-globales|/etc/designs/TAM",
        "/content/configuracion/servicios/config/base|/etc/designs/LAN",
        "/content/configuracion/servicios/config/base|/etc/designs/TAM",
        "/content/configuracion/coutry-selector-repository|/etc/designs/LAN",
        "/content/configuracion/coutry-selector-repository-tam|/etc/designs/TAM",
        "/content/configuracion/configuracion-del-menu|/etc/designs/LAN",
        "/content/configuracion/configuracion-del-menu-tam|/etc/designs/TAM",
        "/content/configuracion/login-profile-action|/etc/designs/LAN",
        "/content/configuracion/login-profile-action-cl|/etc/designs/LAN",
        "/content/configuracion/login-profile-action-en-us|/etc/designs/LAN",
        "/content/configuracion/login-profile-action-tam|/etc/designs/TAM"
        },
        cardinality = Integer.MAX_VALUE,
        label = "Mapping",
        description = "Using the format <content/configuration/path>|<path>, list all mappings between a "
        + "configuration page and either a corresponding path of a site or page design "
        + "(e.g. /content/configuration/variables-globales|/etc/designsLAN). This service will searches"
        + " and publishes all pages having a reference of the content configuration page."),

        @Property(
        name = CacheInvalidationServiceImpl.PN_PURGE_BY_TEMPLATE,
        value = {"/apps/latamApp/templates/noticias|/bin/external/news.json"},
        cardinality = Integer.MAX_VALUE,
        label = "Purge by template and a JSON path",
        description = "Following the format </template/path/>|</json/path>, enter "
        + "a template path and a corresponding json path, when a page based on one of these templates"
        + " is activated, a request will be send to Akamai server and Dispatcher instance in order to "
        + "flush the given json path. (e.g. /apps/latamApp/templates/noticias|/bin/external/news.json)"),*/

        @Property(
        name = CacheInvalidationServiceImpl.PN_PURGE_BY_COMPONENTS,
        value = {
        "metlifeCommons/components/section/forms-library|results"
        },
        cardinality = Integer.MAX_VALUE,
        label = "Purge by components",
        description = "Following the format <component/resource/type>|<jsonSelector> enter a component "
        + "resource type and a corresponding JSON selector, when a page based on one of the templates "
        + "configured in (templatesList) is activated, this service will searches for each component "
        + "listed here to build a json url with the selector provided in order to purge that json url "
        + "(e.g. MetlifeApp/components/section/component|selector)"),

        @Property(
        name = CacheInvalidationServiceImpl.PN_PURGE_TEMPLATES_LIST,
        value = {
        "/apps/metlifeCommons/templates/forms-library"
        },
        cardinality = Integer.MAX_VALUE,
        label = "Templates list",
        description = "When a page based on one of the templates listed here is activated, this service will "
        + "searches for each component set on (purgeByComponents) in order to purge each component "
        + "json url (e.g. /apps/MetlifeApp/templates/general-content)")/*,

        @Property(
        name = CacheInvalidationServiceImpl.PN_CONFIGURATION_PAGES_PATH,
        value = "/content/configuracion",
        label = "Configuration pages path",
        description = "Configuration pages path (e.g. /content/configuracion)")*/
        })

public class CacheInvalidationServiceImpl implements CacheInvalidationService {

    private static final String MAPPED_JCR_CONTENT = "_jcr_content";
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInvalidationServiceImpl.class);

    @Reference
    private Replicator replicator;
    @Reference
    private CcuManager ccuManager;
    @Reference
    private FlushAkamaiItemsJob flushAkamaiItemsJob;

    /** The mapping urls. */
    public static final String PN_MAPPING = "mappingUrls";
    /** The config pages Path. */
    public static final String PN_CONFIGURATION_PAGES_PATH = "configPagesPath";
    /** The purge by template path.*/
    public static final String PN_PURGE_BY_TEMPLATE = "purgeByTemplate";
    /** The template list. */
    public static final String PN_PURGE_TEMPLATES_LIST = "templatesList";
    /** the purge by components. */
    public static final String PN_PURGE_BY_COMPONENTS = "purgeByComponents";

    private static final String PIPE_SEPARATOR = "|";
    private static final String DEFAULT_CONFIGURATION_PAGES_PATH = "/content/configuracion";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, List<String>> filterMapping = new HashMap<>();
    private Map<String, List<String>> purgeByTemplateMap = new HashMap<>();
    private List<String> purgeTemplatesList = Lists.newArrayList();
    private Map<String, List<String>> purgeByComponentsMap = new HashMap<>();

    private String configPagesPath;
    private boolean hasReference;


    /**
     * The activate method.
     * @param context the context
     * @throws Exception the exception
     * */
    @Activate
    protected void activate(final ComponentContext context) throws Exception {
        this.filterMapping = getPropertyMapped(context, PN_MAPPING);
        this.purgeByTemplateMap = getPropertyMapped(context, PN_PURGE_BY_TEMPLATE);
        final String[] templatesListArray =
                PropertiesUtil.toStringArray(context.getProperties().get(PN_PURGE_TEMPLATES_LIST));
        this.purgeTemplatesList = Arrays.asList(templatesListArray);
        this.purgeByComponentsMap = getPropertyMapped(context, PN_PURGE_BY_COMPONENTS);


        this.configPagesPath = PropertiesUtil.toString(context.getProperties().get(PN_CONFIGURATION_PAGES_PATH),
                DEFAULT_CONFIGURATION_PAGES_PATH);
    }


    /**
     * The deactivate method.
     * */
    @Deactivate
    protected void deactivate() {
        filterMapping = Collections.emptyMap();
        purgeByTemplateMap = Collections.emptyMap();
        purgeByComponentsMap = Collections.emptyMap();
        purgeTemplatesList = Collections.emptyList();
        configPagesPath = Constants.BLANK;
    }


    /**
     * Maps an OSGI property values to key/value pairs given a property name and component context.
     *
     * @param context <code>ComponentContext</code> object to get the OSGI property values of the given property name
     * @param propertyName <code>String</code> the property name in the OSGI configuration
     * @return a <code>Map<String, List<String>>></code> containing the mapped values
     */
    private Map<String, List<String>> getPropertyMapped(final ComponentContext context, final String propertyName) {
        final Map<String, List<String>> propertyMapped = new HashMap<>();
        final String[] propertyItems = PropertiesUtil.toStringArray(context.getProperties().get(propertyName));
        if (null != propertyItems && propertyItems.length > 0) {
            for (final String propertyItem : propertyItems) {
                final String[] propertyItemSplitted = propertyItem.split(Pattern.quote(PIPE_SEPARATOR));
                if (propertyItemSplitted.length == 2) {
                    final String filterKey = propertyItemSplitted[KEY_INDEX];
                    final String filterVal = propertyItemSplitted[VALUE_INDEX];
                    if (!StringUtils.isEmpty(filterKey) && !StringUtils.isEmpty(filterVal)) {
                        propertyMapped.put(filterKey, valueToList(propertyMapped, filterKey, filterVal));
                    }
                }
            }
        }
        return propertyMapped;
    }


    /**
     * Checks in a given map whether the given key already exists in the map, if it does, updates the values of that
     * key otherwise add this new key/value pair.
     *
     * @param propertyMapped    a map to be checked
     * @param filterKey         a key searching in the map
     * @param filterValue       the key value
     * @return the list
     */
    private List<String> valueToList(final Map<String, List<String>> propertyMapped,
            final String filterKey, final String filterValue) {

        List<String> result = new ArrayList<>();
        List<String> tmpList = new ArrayList<>();
        if (propertyMapped.containsKey(filterKey)) {
            tmpList = propertyMapped.get(filterKey);
        }
        if (tmpList.size() > 0) {
            for (final String val : tmpList) {
                if (val.equals(filterValue)) {
                    result = tmpList;
                }
            }
            tmpList.add(filterValue);
        } else {
            tmpList.add(filterValue);
        }
        result = tmpList;
        return result;
    }


    /**
     * Gets the value of the property configPagesPath set as an OSGI property.
     *
     * @return a <code>String</code> with the configPagesPath value set on OSGI or the default path
     * /content/configuracion in empty case.
     */
    @Override
    public String getConfigurationPagesPath() {
        return configPagesPath.toLowerCase();
    }



    /**
     * Collects and publishes all pages containing a reference of the content configuration page activated.
     *
     * @param activatedPagePath <code>String</code> the activated page path
     * @param modificationDate <code>Date</code> modificacion date of the page
     * @param resourceResolver <code>ResourceResolver</code> object to geth the page resources
     * @param adminSession <code>Session</code> object with admin privileges in order to publish resources
     */

    @Override
    public void collectAndPublishReferences(final String activatedPagePath, final Date modificationDate,
            final ResourceResolver resourceResolver, final Session adminSession) {

        final List<String> toPublish = new ArrayList<>();
        final Set<String> filterPathKeys = filterMapping.keySet();
        for (final String pathKey : filterPathKeys) {
            if (activatedPagePath.startsWith(pathKey)) {
                final List<String> rootPaths = filterMapping.get(pathKey);
                for (final String rootPath : rootPaths) {
                    final Resource rootResource = resourceResolver.getResource(rootPath);
                    if (null != rootResource) {
                        final List<Resource> rootChildrenResource = getResourceChildren(resourceResolver, rootResource);
                        for (final Resource pageResource : rootChildrenResource) {
                            final Node pageNode = pageResource.adaptTo(Node.class);
                            hasReference = false;
                            checkReferencePaths(pageNode, pathKey);
                            if (hasReference) {
                                if (canReplicate(pageResource, modificationDate, resourceResolver)) {
                                    toPublish.add(pageResource.getPath());
                                }
                            }
                        }
                    }
                }
            }
        }

        publishReferences(toPublish, adminSession);
    }


    /**
     * Lists all page resources children of a given resource.
     *
     * @param resourceResolver <code>ResourceResolver</code> object to get a resource children
     * @param resource <code>Resource</code> a root resource to get its children
     * @return <code>List<Resource></code> a list containing all children resource of the given root resource
     */
    private List<Resource> getResourceChildren(final ResourceResolver resourceResolver, final Resource resource) {
        final List<Resource> resourcesList = new LinkedList<>();
        final Iterator<Resource> resourceIterator = resourceResolver.listChildren(resource);
        while (resourceIterator.hasNext()) {
            final Resource childResource = resourceIterator.next();
            List<Resource> tempList = new ArrayList<>();
            if (NameConstants.NT_PAGE.equals(childResource.getResourceType())) {

                resourcesList.add(childResource);
                tempList = getResourceChildren(resourceResolver, childResource);

            } else if (JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(childResource.getResourceType())
                    || JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(childResource.getResourceType())
                    || JcrConstants.NT_FOLDER.equals(childResource.getResourceType())) {

                tempList = getResourceChildren(resourceResolver, childResource);
            }

            for (final Resource tempItem : tempList) {
                resourcesList.add(tempItem);
            }
        }
        return resourcesList;
    }


    /**
     * Switches the hasReference global flag indicating both the given node or some child node has a reference of the
     * given path key.
     *
     * @param jcrContentNode root <code>Node</code> object to looks for a reference of the given path key
     * @param filterKey <code>String</code> path reference
     */
    private void checkReferencePaths(final Node jcrContentNode, final String filterKey) {
        try {
            final NodeIterator it = jcrContentNode.getNodes();
            while (it.hasNext()) {
                final Node node = it.nextNode();
                final PropertyIterator propIterator = node.getProperties();
                while (propIterator.hasNext()) {
                    final javax.jcr.Property property = propIterator.nextProperty();
                    if (!property.getDefinition().isMultiple()) {
                        final String propVal = property.getValue().getString();
                        if (!StringUtils.isEmpty(propVal) && propVal.startsWith(filterKey)) {
                            hasReference = true;
                            // at this point we already know that this resource has a reference of the given path
                            // so it is unnecessary iterate over the rest of child nodes
                            return;
                        }
                    }
                }

                if (node.hasNodes() && !hasReference) {
                    checkReferencePaths(node, filterKey);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * Publishes all paths given into the toPublish parameter.
     *
     * @param toPublish <code>List</code> containg paths to be published
     * @param adminSession <code>Session</code> object with admin privileges letting publish paths
     */
    private void publishReferences(final List<String> toPublish, final Session adminSession) {
        for (final String path : toPublish) {
            if (null != adminSession && null != replicator) {
                try {
                    replicator.replicate(adminSession, ReplicationActionType.ACTIVATE, path);
                } catch (ReplicationException e) {
                    LOGGER.error("++CacheInvalidationServiceImpl.publishReferences, Page {} could not be replicated++",
                            path);
                }
            }
        }
    }

    /**
     * Checks a list of conditions and return a boolean value indicating if the given resource can be published.
     *
     * @param pageResource <code>Resource</code> object to be checked
     * @param activationDate <code>Date</code> resource activated date
     * @param resourceResolver <code>ResourceResolver</code> object to get the page of the given resource
     * @return true <code>boolean</code> if the resource can be published otherwise false <code>boolean</code>
     */
    private boolean canReplicate(final Resource pageResource, final Date activationDate,
            final ResourceResolver resourceResolver) {

        final ReplicationStatus replicationStatus = pageResource.adaptTo(ReplicationStatus.class);
        final boolean isActivated = replicationStatus.isActivated();
        final boolean hasScheduleActivation = hasScheduledActivation(pageResource, resourceResolver, activationDate);
        return isActivated && !hasScheduleActivation;
    }


    /**
     * Checks if the given resource has a scheduled activation date or the activationDate value is between the start
     * and end of the schedule activation date.
     *
     * @param pageResource <code>Resource</code> object to be checked.
     * @param activationDate <code>Date</code> object indicating the resource activation date
     * @param resourceResolver <code>ResourceResolver</code> object required for internal call to isModified method
     * @return <code>boolean</code> true in the following cases: 1. the resource hasn't a scheduled activation date,
     * 2. the activationDate value is between the start and end of the scheduled activation date, 3. the resource has
     * a start scheduled activation date and hasn't an end of scheduled activation date and the activationDate value
     * is after the start scheduled activation date, otherwise <code>boolean</code> false.
     */
    private boolean hasScheduledActivation(final Resource pageResource, final ResourceResolver resourceResolver,
            final Date activationDate) {
        final Resource pageContentRes = pageResource.getChild(pageResource.getPath() + Constants.SLASH
                + Constants.JCR_CONTENT);
        final ValueMap properties = pageContentRes.adaptTo(ValueMap.class);

        boolean result = true;
        if (!properties.containsKey(NameConstants.PN_ON_TIME) && !properties.containsKey(NameConstants.PN_OFF_TIME)) {
            result = isModified(pageResource.getPath(), resourceResolver);
        } else if (properties.containsKey(NameConstants.PN_ON_TIME)) {
            final Date onTime = properties.get(NameConstants.PN_ON_TIME, Date.class);
            if (null != onTime) {
                if (activationDate.after(onTime)) {
                    result = isModified(pageResource.getPath(), resourceResolver);
                }
            }
        } else if (properties.containsKey(NameConstants.PN_OFF_TIME)) {
            final Date offTime = properties.get(NameConstants.PN_OFF_TIME, Date.class);
            if (null != offTime) {
                if (activationDate.before(offTime)) {
                    result = false;
                }
            }
        } else if (properties.containsKey(NameConstants.PN_ON_TIME)
                && properties.containsKey(NameConstants.PN_OFF_TIME)) {
            final Date onTime = properties.get(NameConstants.PN_ON_TIME, Date.class);
            final Date offTime = properties.get(NameConstants.PN_OFF_TIME, Date.class);
            if (null != onTime && null != offTime) {
                if (activationDate.after(onTime) && activationDate.before(offTime)) {
                    result = false;
                }
            }
        }

        return result;
    }


    /**
     * Checks if a page is in modified status.
     *
     * @param path <code>String</code> the page path
     * @param resourceResolver <code>ResourceResolver</code> object to get a page with the given path
     * @return <code>boolean</code> true is the page is in modified status otherwise <code>boolean</code> false
     */
    private boolean isModified(final String path, final ResourceResolver resourceResolver) {
        final Page page = getPage(path, resourceResolver);
        long lastModified;
        long lastPublished;
        boolean result = false;
        if (null != page) {
            if (page.getLastModified() == null) {
                lastModified = -1;
            } else {
                lastModified = page.getLastModified().getTimeInMillis();
            }
            lastPublished = getLastPublishedPage(path, resourceResolver);
            result =  lastModified > lastPublished;
        }

        return result;
    }


    /**
     * Gets the last published date of a page.
     *
     * @param pagePath <code>String</code> the page path
     * @param resourceResolver <code>ResourceResolver</code> object to get a page with the given path
     * @return <code>long</code> value with the last published date of publication
     */
    private long getLastPublishedPage(final String pagePath, final ResourceResolver resourceResolver) {
        final Resource resource = resourceResolver.getResource(pagePath);
        final ReplicationStatus replicationStatus = resource.adaptTo(ReplicationStatus.class);
        long result = 0;
        // getLastPublished method return null when a page has been created but not published
        if (null != replicationStatus.getLastPublished()) {
            result = replicationStatus.getLastPublished().getTimeInMillis();
        }
        return result;
    }


    /**
     * Gets a page given a path.
     *
     * @param path <code>String</code> the page path
     * @param resourceResolver <code>ResourceResolver</code> object to get a page object
     * @return <code>Page</code> object
     */
    private Page getPage(final String path, final ResourceResolver resourceResolver) {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        return pageManager.getPage(path);
    }


    /**
     * Checks if the activated page is in the filter set in purgeByTemplate OSGI property and calls a couple method
     * cleaning the dispatcher cache and purging the akamai cache.
     *
     * @param activatedPagePath <code>String</code> the activated page path
     * @param resourceResolver <code>ResourceResolver</code> object to get a page object
     */
    @Override
    public void checkTemplate(final String activatedPagePath, final ResourceResolver resourceResolver) {
        final Page page = getPage(activatedPagePath, resourceResolver);
        if (null != page) {
            final String templateProperty = page.getProperties().get(NameConstants.PN_TEMPLATE, Constants.BLANK);
            for (final String templateKey : purgeByTemplateMap.keySet()) {
                if (!StringUtils.isEmpty(templateProperty) && templateProperty.equals(templateKey)) {
                    flushDispatcher(templateKey);
                    purgeAkamaiCacheByResource(activatedPagePath, templateKey);
                }
            }

            purgeJsonByTemplateAndComponents(templateProperty, page, resourceResolver);
        }
    }


    /**
     * Checks if the given page is in templates list configured in OSGI console, if it does, gets all components
     * of this page as component-path/selector map.
     *
     * @param templatePath <code>String</code> the page template
     * @param page <code>Page</code> either the current activated page or a page requested to be purged by the servlet
     *             'PurgeAkamaiServlet'.
     * @param resourceResolver <code>ResourceResolver</code> object to resolve resources
     */
    public void purgeJsonByTemplateAndComponents(final String templatePath, final Page page,
            final ResourceResolver resourceResolver) {

        LOGGER.debug("**** purgeJsonByTemplateAndComponents");
        for (final String templateKey : this.purgeTemplatesList) {
            LOGGER.debug("+++++ procesing templatePath " + templatePath);
            LOGGER.debug("+++++ procesing templateKey: " + templateKey);
            if (!StringUtils.isEmpty(templatePath) && templatePath.equals(templateKey)) {
                final Map<String, String> componentPaths = getComponentPaths(page, resourceResolver);
                flushDispatcherJSON(componentPaths);
                purgeAkamaiJSON(componentPaths, page.getPath());
            }
        }
    }


    /**
     * Iterates over a given map containing component paths as key and a json selector as value, build a full json url
     * in order to be flushed.
     *
     * @param componentPathsMap <code>Map</code> containing component paths as key and a json selector as value
     */
    private void flushDispatcherJSON(final Map<String, String> componentPathsMap) {
        for (final String componentPath : componentPathsMap.keySet()) {
            for (final Object dispatcher : flushAkamaiItemsJob.dispatchersToList()) {
                String fullJsonUrl = componentPath + Constants.DOT + componentPathsMap.get(componentPath)
                        + Constants.JSON_EXT;
                fullJsonUrl = fullJsonUrl.replace(Constants.JCR_CONTENT, MAPPED_JCR_CONTENT);
                LOGGER.debug("+++ Dispatcher, json to flush: " + fullJsonUrl);
                final DispatcherFlush dispatcherFlush = new DispatcherFlush((String) dispatcher, fullJsonUrl);
                final String response = dispatcherFlush.flush();
                LOGGER.debug("+++ Dispatcher response: " + response);
            }
        }
    }


    /**
     * Iterates over a given map containing component paths and json selector, build a full json url in order to
     * be purged.
     *
     * @param componentPathsMap <code>Map</code> containing component paths as key and a json selector as value
     * @param pagePath <code>String</code> the current page path
     */
    private void purgeAkamaiJSON(final Map<String, String> componentPathsMap, final String pagePath) {
        final List<String> domainsList = flushAkamaiItemsJob.getRootUrl(pagePath);

        if (null != domainsList) {
            for (final String domain : domainsList) {
                for (final String componentPath : componentPathsMap.keySet()) {
                    String fullTableUrl = domain + Constants.SLASH + getTrimmedUrl(componentPath) + Constants.DOT
                            + componentPathsMap.get(componentPath) + Constants.JSON_EXT;
                    fullTableUrl = fullTableUrl.replace(Constants.JCR_CONTENT, MAPPED_JCR_CONTENT);

                    LOGGER.debug("+++ Akamai, json to purge: " + fullTableUrl);
                    final PurgeResponse purgeResponse = ccuManager.purgeByUrl(fullTableUrl);
                    LOGGER.debug("+++ Akamai response: "
                            + "Purge request status: " + purgeResponse.httpStatus + " Detail: " + purgeResponse.detail
                            + " Estimated seconds: " + purgeResponse.estimatedSeconds
                            + " Progress uri: " + purgeResponse.progressUri);
                }
            }
        }
    }

    /**
     * Trims the given url returning a new url starting with the site name
     * e.g. given the following path: /content/latam/lan/es_cl/informacion/tablaresponsive
     * returns a new path as follow: es_cl/informacion/tablaresponsive
     *
     * @param url <code>String</code> the url to be trimmed
     * @return <code>String</code> a trimmed url starting with the site name
     */
    private String getTrimmedUrl(final String url) {
        final int iterations = 4;
        String tmpUrl = url;
        for (int i = 0; i < iterations; i++) {
            tmpUrl = tmpUrl.substring(tmpUrl.indexOf(Constants.SLASH) + 1, tmpUrl.length());
        }
        return tmpUrl;
    }

    /**
     * Returns a component-path/json-selector map with all components and selectors configured on OSGI console of the
     * given page.
     *
     * @param page <code>Page</code> the current page
     * @param resourceResolver <code>ResourceResolver</code> to handle page resources
     * @return a key/value <code>Map<String, String></code> containing each component path as key and the json selector
     * as value
     */
    private Map<String, String> getComponentPaths(final Page page, final ResourceResolver resourceResolver) {
        final Map<String, String> components = Maps.newHashMap();

        final Node pageNode = page.adaptTo(Node.class);
        if (null != pageNode) {
            try {
                if (pageNode.hasNode(Constants.JCR_CONTENT)) {
                    final Node contentNode = pageNode.getNode(Constants.JCR_CONTENT);
                    final List<Node> childNodes = this.getAllChildNodes(contentNode);
                    for (final Node childNode : childNodes) {
                        if (childNode.hasProperties()) {
                            final Resource res = resourceResolver.getResource(childNode.getPath());
                            final ValueMap properties = res.adaptTo(ValueMap.class);
                            if (properties.containsKey(Constants.SLING_RESOURCE_TYPE)) {
                                if (this.purgeByComponentsMap.keySet().contains(
                                        properties.get(Constants.SLING_RESOURCE_TYPE))) {
                                    final String componentPath = childNode.getPath();
                                    final List<String> jsonSelectors =
                                            this.purgeByComponentsMap.get(
                                            properties.get(Constants.SLING_RESOURCE_TYPE));
                                    components.put(componentPath, jsonSelectors.get(0));
                                }
                            }
                        }
                    }
                }
            } catch (RepositoryException re) {
                LOGGER.error("CacheInvalidationServiceImpl.getComponentPathsList, Error1: ", re);
            } catch (Exception e) {
                LOGGER.error("CacheInvalidationServiceImpl.getComponentPathsList, Error2: ", e);
            }
        }

        return components;
    }


    /**
     * Extracts all child nodes of a given node.
     *
     * @param node <code>Node</code> the root node
     * @return <code>List<Node></code> object with all child nodes of root node
     */
    private List<Node> getAllChildNodes(final Node node) {
        final List<Node> nodesList = new LinkedList<>();
        try {
            final NodeIterator it = node.getNodes();
            while (it.hasNext()) {
                final Node currNode = it.nextNode();
                nodesList.add(currNode);
                if (currNode.hasNodes()) {
                    final List<Node> tmpList = getAllChildNodes(currNode);
                    for (final Node childNodes : tmpList) {
                        nodesList.add(childNodes);
                    }
                }
            }
        } catch (RepositoryException re) {
            LOGGER.error("CacheInvalidationServiceImpl.getChildNodes, Error: " + re.getMessage());
        }

        return nodesList;
    }


    /**
     * Sends a request to the dispatcher in order to flush the cache.
     *
     * @param templateKey <code>String</code> a resource path acting as a key in order to get a list of urls to be
     *                    flushed
     */
    private void flushDispatcher(final String templateKey) {
        final List<String> urlsToFlush = purgeByTemplateMap.get(templateKey);
        for (final String urlToFlush : urlsToFlush) {
            final String finalUrlToFlush = urlToFlush.replace(Constants.JCR_CONTENT, MAPPED_JCR_CONTENT);
            for (final Object dispatcher : flushAkamaiItemsJob.dispatchersToList()) {
                LOGGER.debug("+++ Url to be flushed: " + finalUrlToFlush);
                final DispatcherFlush dispatcherFlush = new DispatcherFlush((String) dispatcher, finalUrlToFlush);
                final String response = dispatcherFlush.flush();
                LOGGER.debug("+++ Dispatcher flush response: " + response);
            }
        }
    }


    /**
     * Purges the akamai cache given a resource path.
     * @param activatedPagePath <code>String</code> the activated page path must be in the purgeByTemplate property
     *                          set on OSGI acting as a key in order to get a domain list where akamai has to
     *                          purge a resource list
     * @param templateKey <code>String</code> a resource path acting as a key in order to get a list of urls to be
     *                    purged
     */
    private void purgeAkamaiCacheByResource(final String activatedPagePath, final String templateKey) {
        final List<String> urlsToPurge = purgeByTemplateMap.get(templateKey);
        final List<String> domainsList = flushAkamaiItemsJob.getRootUrl(activatedPagePath);
        if (null != domainsList) {
            for (final String domain : domainsList) {
                for (final String urlToPurge : urlsToPurge) {
                    final String finalUrlToPurge = urlToPurge.replace(Constants.JCR_CONTENT, MAPPED_JCR_CONTENT);
                    LOGGER.debug("+++ purgeAkamaiCacheByResource, Url to purge: " + domain + finalUrlToPurge);
                    final PurgeResponse purgeResponse = ccuManager.purgeByUrl(domain + finalUrlToPurge);
                    LOGGER.debug("+++ purgeAkamaiCacheByResource, Akamai response: "
                            + "Purge request status: " + purgeResponse.httpStatus + " Detail: " + purgeResponse.detail
                            + " Estimated seconds: " + purgeResponse.estimatedSeconds
                            + " Progress uri: " + purgeResponse.progressUri);
                }
            }
        }
    }

}
