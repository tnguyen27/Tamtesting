package com.metlife.commons.servlet;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.common.collect.Lists;
import com.metlife.commons.services.CacheInvalidationService;
import com.metlife.commons.util.DispatcherFlush;
import com.metlife.commons.util.JsonUtils;
import com.xumak.aem.akamai.ccu.CcuManager;
import com.xumak.aem.akamai.ccu.PurgeResponse;
import com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob;
import com.xumak.base.Constants;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dispatcher Flush.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2015/01/26 | Chepe         | Initial Creation
 * 1.1     | 2016/07/28 | Lesly Quinonez| Adapt to Metlife.
 * ----------------------------------------------------------------------------
 */
@SlingServlet(paths = "/bin/akamaiConnector/purge", methods = "POST", metatype = true)
public class PurgeAkamaiServlet extends org.apache.sling.api.servlets.SlingAllMethodsServlet {
    private static final long serialVersionUID = 2598426539166789515L;
    private static final String HTTP_EXTENSION = ".html";
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final String FAIL_MSG = "Fail";
    private static final String SUCCESS_MSG = "Success";
    private static final String DELIMETER = "<br>";
    private static final String SEPARATOR = " - ";

    @Reference
    private CcuManager ccuManager;

    @Reference
    private FlushAkamaiItemsJob flushAkamaiItemsJob;

    @Reference
    private CacheInvalidationService cacheInvalidationService;

    private Set<String> pathSet = new HashSet<String>();

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        try {
            String status = FAIL_MSG;
            final StringBuffer message = new StringBuffer(Constants.BLANK);
            final StringBuffer urlHost = new StringBuffer(Constants.BLANK);
            final StringBuffer urlPath = new StringBuffer(Constants.BLANK);
            final StringBuffer urlFullPath = new StringBuffer(Constants.BLANK);
            final PrintWriter out = response.getWriter();
            final List<String> preResourcePath = new LinkedList<>();
            List<String> resourcePath = new LinkedList<>();
            final JsonGenerator jsonGenerator = new JsonFactory().createGenerator(out);
            jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
            final List<Map<String, String>> jsonArrayResult = Lists.newArrayList();

            final String childs = request.getParameter("child");
            boolean childsRadio = false;

            if (null != childs && childs.contains("checked")) {
                childsRadio = true;
            }

            final StringBuffer path = new StringBuffer(request.getParameter("path"));

            if (null != path) {
                getCurrentPath(message, path);

                final URL url = new URL(path.toString());
                urlHost.append(getUrlInformation(url));
                urlPath.append(addUrlItem(url.getPath(), Constants.BLANK, Constants.BLANK));
                urlFullPath.append(getUrlFullPath(url));

                final List<String> pathFromConfList;
                if (flushAkamaiItemsJob.isRootUrlInConf(urlHost.toString())) {

                    message.append("Domain found in the configuration." + DELIMETER);
                    pathFromConfList = flushAkamaiItemsJob.getPathFromMapping(urlHost.toString());
                    preResourcePath.add(urlPath.toString());
                    log.info("pathFromConfList => " + pathFromConfList);

                    addVerifyPaths(pathFromConfList, urlPath.toString(), preResourcePath);

                    log.info("preResourcePath (STAGE4) => " + preResourcePath);

                    addResourcePath(preResourcePath, resourcePath);

                    log.info("resourcePathList => " + resourcePath);

                    // Check if the paths are in the configuration
                    resourcePath = getTempList(resourcePath, pathFromConfList);
                    if (resourcePath.size() == 0) {
                        message.append("Paths are not in the configuration for the Domain" + DELIMETER);
                    }

                    // In this point we have a solid AEM PATH. We still don't know if it exists
                    // but is the best we can do from the parsing.
                    final List<String> duplicatesCheckList = new ArrayList<>();
                    for (final String resourcePathValue : resourcePath) {

                        if (StringUtils.isNotEmpty(resourcePathValue)) {

                            final ResourceResolver resourceResolver = request.getResourceResolver();
                            final Resource resource = resourceResolver.getResource(resourcePathValue);
                            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

                            // If resource is different from null is that it found the resource,
                            // otherwise it couldn't find a mapping.
                            if (null != resource) {
                                message.append("Path found in AEM." + DELIMETER);

                                if (childsRadio) {

                                    final List<String> childURLs = getChildResources(resourceResolver, resource);
                                    message.append("Children resources " + childURLs.size() + DELIMETER);

                                    if (childURLs.size() <= flushAkamaiItemsJob.getChildLimit()) {

                                        for (final String childURL : childURLs) {

                                            // addValues for structure Nodes Begin
                                            final Page childPage = pageManager.getPage(childURL);

                                            purgeJsonByComponents(childPage, resourceResolver);
                                            Template templateObj = null;
                                            if (childPage == null) {
                                                templateObj = childPage.getTemplate();
                                            }

                                            if (templateObj != null && flushAkamaiItemsJob
                                                    .isTemplatePathInConfTemplates(templateObj.getPath())) {
                                                final List<String> structutureNodeList = flushAkamaiItemsJob
                                                        .getFullListPathsForTemplatePathInConfTemplates(templateObj
                                                        .getPath(), childPage.getPath(), false, true, "");

                                                // Adding all structure nodes
                                                for (final String structureNode : structutureNodeList) {
                                                    final String postProcessPath = resourceResolver.map(structureNode);

                                                    final Set<String> localPathSet = new HashSet<String>();
                                                    localPathSet.add(postProcessPath);

                                                    final Set<String> absoluteUrls = flushAkamaiItemsJob
                                                            .prependPathWithRootUrl(localPathSet, urlHost.toString());

                                                    for (final String i : absoluteUrls) {
                                                        purgeResource(i, childURL, jsonArrayResult);
                                                    }
                                                }
                                            }
                                            // addValues for structure Nodes END

                                            final String postProcessPath = resourceResolver.map(childURL);

                                            final Set<String> localPathSet = new HashSet<String>();
                                            localPathSet.add(postProcessPath);

                                            final Set<String> absoluteUrls =
                                                    flushAkamaiItemsJob.prependPathWithRootUrl(localPathSet,
                                                    urlHost.toString());

                                            for (final String i : absoluteUrls) {
                                                purgeResource(i, childURL, jsonArrayResult);
                                            }
                                        }
                                    } else {
                                        message.append("Childrens.size exceeds limit allowed( ")
                                                .append(flushAkamaiItemsJob.getChildLimit()).append(" )");
                                    }

                                }

                                // countChilds to make sure < 100
                                // addValues for structure Nodes Begin
                                final Page currentPage = pageManager.getPage(resourcePathValue);

                                purgeJsonByComponents(currentPage, resourceResolver); // purge tables json cache

                                Template templateObj = null;
                                if (currentPage != null) {
                                    templateObj = currentPage.getTemplate();
                                }

                                if (templateObj != null && flushAkamaiItemsJob.isTemplatePathInConfTemplates(
                                        templateObj.getPath())) {

                                    final List<String> structutureNodeList = flushAkamaiItemsJob.
                                            getFullListPathsForTemplatePathInConfTemplates(templateObj.getPath(),
                                            resource.getPath(), false, true, "");

                                    // Adding all structure nodes
                                    for (final String structureNode : structutureNodeList) {
                                        final String postProcessPath = resourceResolver.map(structureNode);

                                        final Set<String> localPathSet = new HashSet<String>();
                                        localPathSet.add(postProcessPath);

                                        final Set<String> absoluteUrls =
                                                flushAkamaiItemsJob.prependPathWithRootUrl(localPathSet,
                                                urlHost.toString());

                                        for (final String i : absoluteUrls) {
                                            purgeResource(i, resourcePathValue, jsonArrayResult);
                                        }
                                    }
                                }
                                // addValues for structure Nodes END
                                purgeCombinationURLs(path.toString(),
                                        resourcePathValue, jsonArrayResult, duplicatesCheckList);
                                status = SUCCESS_MSG;
                            } else {
                                //message.append("Path not found in AEM." + DELIMETER);
                                if (!isDuplicatedPath(duplicatesCheckList, path.toString())) {
                                    purgeCombinationURLs(path.toString(),
                                            resourcePathValue, jsonArrayResult, duplicatesCheckList);
                                }

                                status = SUCCESS_MSG;
                            }
                        }
                    }
                } else {
                    message.append("Domain not found in the configuration." + DELIMETER);
                }
            } else {
                message.append("Path sent to the service is null.");
            }

            //Encode the submitted form data to JSON
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("path", path.toString());
            jsonGenerator.writeStringField("urlHost", urlHost.toString());
            jsonGenerator.writeStringField("urlPath", urlPath.toString());
            jsonGenerator.writeStringField("urlFullPath", urlFullPath.toString());
            JsonUtils.addList(jsonGenerator, "preResourcePath", preResourcePath);
            JsonUtils.addList(jsonGenerator, "resourcePath", resourcePath);
            jsonGenerator.writeBooleanField("childs", childsRadio);
            jsonGenerator.writeStringField("childsStr", childs);
            jsonGenerator.writeStringField("status", status);
            jsonGenerator.writeStringField("message", message.toString());
            JsonUtils.addListMap(jsonGenerator, "results", jsonArrayResult);


            //Return the JSON formatted data
            jsonGenerator.writeEndObject();
            jsonGenerator.flush();
            jsonGenerator.close();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets URL Information.
     * @param url url object
     * @return string with the url information.
     */
    private String getUrlInformation(final URL url) {
        final StringBuffer urlHost = new StringBuffer(Constants.BLANK);
        urlHost.append(addUrlItem(url.getProtocol(), Constants.BLANK, "://"))
                .append(addUrlItem(url.getUserInfo(), Constants.BLANK, "@"))
                .append(addUrlItem(url.getHost(), Constants.BLANK, Constants.BLANK))
                .append(addUrlItem(url.getPort(), ":", Constants.BLANK));
        return urlHost.toString();
    }

    /**
     * Gets the full path from the URL.
     * @param url url object.
     * @return full path from the url information.
     */
    private String getUrlFullPath(final URL url) {
        final StringBuffer urlFullPath = new StringBuffer(Constants.BLANK);
        urlFullPath.append(addUrlItem(url.getPath(), Constants.BLANK, Constants.BLANK))
                .append(addUrlItem(url.getQuery(), "?", Constants.BLANK))
                .append(addUrlItem(url.getRef(), "#", Constants.BLANK));
        return urlFullPath.toString();
    }

    /**
     * Checks if the checklist contains the current path.
     * @param duplicatesCheckList checklist
     * @param path current path
     * @return true if the checklist contains the path.
     */
    private boolean isDuplicatedPath(final List<String> duplicatesCheckList, final String path) {
        boolean isDuplicate = false;
        for (final String value : duplicatesCheckList) {
            if (value.equals(path)) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    /**
     * Adds all the resource paths on the resourcePath List.
     * @param preResourcePath list of pre resource paths
     * @param resourcePath list of resource paths
     */
    private void addResourcePath(final List<String> preResourcePath, final List<String> resourcePath) {
        for (final String preResourcePathString : preResourcePath) {
            if (preResourcePathString.endsWith(Constants.SLASH)) { // Homepage
                resourcePath.add(preResourcePathString);
            } else if (flushAkamaiItemsJob.isExtensionInConf(preResourcePathString)
                    || preResourcePathString.endsWith(HTTP_EXTENSION)) {
                resourcePath.add(preResourcePathString.
                        substring(0, preResourcePathString.lastIndexOf('.')));
                log.debug("status => STAGE5");

            }
        }
    }

    /**
     * Checks if the paths are in the configuration.
     * @param resourcePath List of resource paths
     * @param pathFromConfList list of configurations paths.
     * @return list of temporarily paths.
     */
    private List<String> getTempList(final List<String> resourcePath, final List<String> pathFromConfList) {

        final List<String> tempList = new LinkedList<>();
        for (final String resourcePathValue : resourcePath) {
            for (final String value : pathFromConfList) {
                if (resourcePathValue.startsWith(value)) {
                    tempList.add(resourcePathValue);
                }
            }
        }
        return tempList;
    }

    /**
     * Purges the different combinations that the URL has.
     * @param path path to purge
     * @param resourcePathValue Resource path value
     * @param jsonArrayResult json array
     * @param duplicatesCheckList list of URLs
     */
    private void purgeCombinationURLs(final String path, final String resourcePathValue,
            final List jsonArrayResult, final List<String> duplicatesCheckList) {
        final List<String> urlsAllCombinationsExtensions = flushAkamaiItemsJob.getAllURLCombinations(path);
        for (final String singleUrlCombinationExtension : urlsAllCombinationsExtensions) {
            purgeResource(singleUrlCombinationExtension, resourcePathValue, jsonArrayResult);
            duplicatesCheckList.add(singleUrlCombinationExtension);
        }
    }

    /**
     * Verifies and adds the paths that begins with spected regex.
     * @param pathFromConfList List of paths configured
     * @param urlPath current path
     * @param preResourcePath pre resource path
     */
    private void addVerifyPaths(final List<String> pathFromConfList, final String urlPath,
            final List<String> preResourcePath) {
        for (final String verifyPath : pathFromConfList) {
            if (!urlPath.startsWith(verifyPath) && !urlPath.startsWith("/content")
                    && !urlPath.startsWith("/etc")) {
                preResourcePath.add(verifyPath + urlPath);
            }
        }
    }

    /**
     * Checks and appends the right path structure.
     * @param message logger
     * @param path resource path
     */
    private void getCurrentPath(final StringBuffer message, final StringBuffer path) {
        if (!path.toString().startsWith(HTTP_PROTOCOL) && !path.toString().startsWith(HTTPS_PROTOCOL)) {
            path.append(HTTP_PROTOCOL).append(path);
            message.append("Path doesn' start with http or https, adding it." + DELIMETER);
        }
    }

    /**
     * .
     * @param path Akamai Path
     * @param resourcePath resouce path
     * @param jsonArrayResult result array
     */
    private void purgeResource(
            final String path,
            final String resourcePath,
            final List jsonArrayResult) {
        PurgeResponse akamairesponse = null;
        final StringBuffer responseAll = new StringBuffer(Constants.BLANK);

        for (final Object disp : flushAkamaiItemsJob.dispatchersToList()) {
            final DispatcherFlush dispatcherFlush = new DispatcherFlush((String) disp, resourcePath);
            String response = dispatcherFlush.flush();
            if (null != response) {
                response = response.trim();
            }
            responseAll.append("Dispatcher: ")
                    .append((String) disp)
                    .append(" - Response: ")
                    .append(response)
                    .append("<br>");
            log.info("Response from dispatcher " + (String) disp + " and path " + resourcePath + " = " + response);
        }

        final String rewrittenPath = flushAkamaiItemsJob.rewriteInternalLink(path);

        pathSet.clear();
        pathSet.add(rewrittenPath);

        akamairesponse = ccuManager.purgeByUrls(pathSet);

        responseAll.append("Akamai Response: ")
                .append(akamairesponse.httpStatus)
                .append(SEPARATOR).append(akamairesponse.detail)
                .append(SEPARATOR).append(akamairesponse.estimatedSeconds)
                .append(SEPARATOR).append(akamairesponse.progressUri);

        final Map<String, String> res = new HashedMap();
        res.put("url", rewrittenPath);
        res.put("response", responseAll.toString());
        jsonArrayResult.add(res);

//        if( rewrittenPath.endsWith( ".html" ) ) {
//            pathSet.clear();
//            pathSet.add(rewrittenPath.substring(0, rewrittenPath.lastIndexOf('.')));
//            akamairesponse = ccuManager.purgeByUrls( pathSet );
//
//            responseAll = "";
//            responseAll += "Akamai Response: "
//                    + akamairesponse.httpStatus
//                    + SEPARATOR + akamairesponse.detail
//                    + SEPARATOR + akamairesponse.estimatedSeconds
//                    + SEPARATOR + akamairesponse.progressUri;
//
//            res = new JSONObject();
//            res.put( "url", rewrittenPath.substring( 0, rewrittenPath.lastIndexOf( '.' ) ) );
//            res.put( "response", responseAll );
//            jsonArrayResult.add( res );
//        }
    }

    /**
     * Gets the child resource object.
     * @param resolver resolver object
     * @param resource resource object
     * @return child resource
     */
    private List<String> getChildResources(final ResourceResolver resolver, final Resource resource) {
        final List<String> childResources = new LinkedList<String>();

        final Iterator<Resource> iter = resolver.listChildren(resource);
        while (iter.hasNext()) {
            final Resource childResource = iter.next();
            log.info("resource  " + childResource.getName() + " - " + childResource.getResourceType());
            if (NameConstants.NT_PAGE.equals(childResource.getResourceType())) {
                childResources.add(childResource.getPath());
                log.info("childResources.add(childResource.getPath()) " + childResource.getPath());
                final List<String> tempList = getChildResources(resolver, childResource);

                for (final String tempItem : tempList) {
                    childResources.add(tempItem);
                }
            }
        }

        return childResources;
    }

    /**
     * Adds the URL of the item.
     * @param item element
     * @param prefix .
     * @param postfix .
     * @return URL
     */
    private String addUrlItem(final String item, final String prefix, final String postfix) {
        String result = "";

        if (null != item && StringUtils.isNotEmpty(item)) {
            result = prefix + item + postfix;
        }

        return result;
    }

    /**
     * Add the URL of the item.
     * @param item .
     * @param prefix .
     * @param postfix .
     * @return URL
     */
    private String addUrlItem(final int item, final String prefix, final String postfix) {
        String result = "";

        if (-1 != item) {
            result = prefix + item + postfix;
        }

        return result;
    }

    /**
     * Looks for table components in order to purge each json path corresponding to each table found in the given page.
     *
     * @param page <code>Page</code> representing the current page
     * @param resourceResolver <code>ResourceResolver</code> object to get a page object
     */
    public void purgeJsonByComponents(final Page page, final ResourceResolver resourceResolver) {
        if (null != page) {
            final String templatePath = page.getProperties().get(NameConstants.PN_TEMPLATE, Constants.BLANK);
            log.debug("Looking for components in: " + page.getPath());
            cacheInvalidationService.purgeJsonByTemplateAndComponents(templatePath, page, resourceResolver);
        }
    }
}
