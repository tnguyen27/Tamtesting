package com.metlife.commons.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.metlife.poc.util.MetLifeConstants;

/**
 * Created by j.amorataya on 2/1/16.
 */
@Component
@Service
public class AddArchiveResultsContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * BASE_PATH.
     */
    private static final String BASE_PATH = "rootPath";
    /**
     * TOPIC_PROPERTY.
     */
    private static final String TOPIC_PROPERTY = "topics";
    /**
     * LIST_PATHS_PROPERTY.
     */
    private static final String LIST_PATHS_PROPERTY = "list.paths";
    /**
     * ARCHIVE_SELECTOR.
     */
    private static final String ARCHIVE_SELECTOR = "archive";
    /**
     * MONTH.
     */
    private static final String MONTH = "month";
    /**
     * YEAR.
     */
    private static final String YEAR = "year";
    /**
     * TOPIC.
     */
    private static final String TOPIC = "topic";
    /**
     * NULL.
     */
    private static final String NULL = "null";

    /**
     * requiredResourceType.
     * @return String
     */
    public final String requiredResourceType() {
        return MetLifeConstants.NEWS_ROOM_ARCHIVE_RESOURCE_TYPE;
    }
    /**
     * This method is used to get all the configuration properties required
     * on the context processor.
     * Override this function to add the configuration properties.
     *
     * @return List of required configuration properties
     */
    public final Set<String> requiredPropertyNames() {
        return Collections.EMPTY_SET;
    }


    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
            ContentModel contentModel) throws Exception {
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        final RequestParameter monthParameter = request.getRequestParameter(MONTH);
        final RequestParameter yearParameter = request.getRequestParameter(YEAR);
        final RequestParameter topicParameter = request.getRequestParameter(TOPIC);

        String month = Constants.BLANK;
        String year = Constants.BLANK;
        String topicSelector = Constants.BLANK;

        if (selectors.length > 0 && selectors[0].equals(ARCHIVE_SELECTOR)) {

            if (yearParameter != null && !yearParameter.getString().isEmpty()
                    && !yearParameter.getString().equals(NULL)) {
                year = yearParameter.getString();
            }
            if (monthParameter != null && !monthParameter.getString().isEmpty()
                    && !monthParameter.getString().equals(NULL)) {
                month = monthParameter.getString();
            }
            if (topicParameter != null && !topicParameter.getString().isEmpty()
                    && !topicParameter.getString().equals(NULL)) {
                topicSelector = topicParameter.getString();
            }

            final String basePath;
            if (contentModel.has(Constants.DESIGN_PROPERTIES_KEY
                    + Constants.DOT + BASE_PATH)) {
                basePath = (String) contentModel.get(
                        Constants.DESIGN_PROPERTIES_KEY
                                + Constants.DOT + BASE_PATH);
            } else {
                basePath = (String) contentModel.get(Constants.PAGE
                        + Constants.DOT
                        + Constants.PATH);
            }
            final ArrayList<String> pathList = new ArrayList<String>();

            if (year != null && !year.isEmpty()) {
                filterResultsByMonth(month, basePath, year, topicSelector, pathList, request);
            } else {
                final Resource basePathPage = request.getResourceResolver().getResource(basePath);
                if (basePathPage != null) {
                    //Iterate through year pages
                    final Iterator<Resource> yearPages = basePathPage.listChildren();
                    while (yearPages.hasNext()) {
                        final Resource yearPage = yearPages.next();
                        filterResultsByMonth(month, basePath, yearPage.getName(), topicSelector, pathList, request);
                    }
                }
            }
            contentModel.set(LIST_PATHS_PROPERTY, pathList);
        }


    }

    /**
     * This method filters the pages by month.
     * @param month name of the month to filter, if this is empty it will include all months
     * @param basePath this is the root path of the pages that contain the articles structure
     * @param year name of the year to filter
     * @param topicSelector topic to filter
     * @param pathList list of paths that match the filters values
     * @param request request to get the resource resolver
     */
    private void filterResultsByMonth(final String month, final String basePath, final String year,
            final String topicSelector, final ArrayList<String> pathList,
            final SlingHttpServletRequest request) {
        if (month.isEmpty()) {

            final Resource resource =
                    request.getResource().getResourceResolver().getResource(basePath + Constants.SLASH + year);
            if (resource != null) {

                final Iterator<Resource> monthsResources = resource
                        .listChildren();
                while (monthsResources.hasNext()) {
                    final Resource singleMonthResource = monthsResources
                            .next();
                    addChildrenPaths(singleMonthResource, pathList,
                            topicSelector);
                }
            }
        } else {
            final Resource resource =
                    request.getResource().getResourceResolver().getResource(basePath + Constants.SLASH + year
                    + Constants.SLASH + month);
            addChildrenPaths(resource, pathList, topicSelector);

        }
    }

    /**
     * This method adds the children paths.
     * @param resource The resource where the paths are going to be added
     * @param pathList A list of paths
     * @param topicSelector The selector
     */
    private void addChildrenPaths(final Resource resource,
            final ArrayList<String> pathList,
            final String topicSelector) {

        if (resource != null) {
            final Iterator<Resource> children = resource.listChildren();
            while (children.hasNext()) {
                final Resource child = children.next();
                if (child.isResourceType(MetLifeConstants.CQ_PAGE)) {
                    final Resource res = child.getChild(Constants.JCR_CONTENT);

                    final ValueMap vmap = res.adaptTo(ValueMap.class);
                    final Object topicProperty = vmap.get(
                            TOPIC_PROPERTY);
                    String[] topics;
                    if (topicProperty instanceof String) {
                        topics = ((String) topicProperty).split(MetLifeConstants.COMMA);

                    } else {
                        topics = (String[]) topicProperty;
                    }
                    if (vmap.containsKey(TOPIC_PROPERTY)) {
                        Boolean hasTopic = false;
                        if (topicSelector.isEmpty()) {
                            hasTopic = true;
                        } else {


                            for (final String s : topics) {
                                if (s.indexOf(topicSelector) >= 0) {
                                    hasTopic = true;

                                }
                            }
                        }
                        if (hasTopic) {
                            pathList.add(child.getPath());

                        }
                    }
                    vmap.size();
                }


            }
        }


    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.HIGHER_PRIORITY;
    }
}
