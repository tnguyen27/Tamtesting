package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Sets;
import com.metlife.poc.util.JSONUtils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SubNavContextProcessor.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 18/12/2015 | Jorge Hernández | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class SubNavContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {
    /**
     * CATEGORY_SUBNAV_RESOURCE_TYPE.
     */
    public static final String CATEGORY_SUBNAV_RESOURCE_TYPE =
            "MetlifeApp/components/section/category-subnav";
    /**
     * SITEMAP_COLUMN_RESOURCE_TYPE.
     */
    public static final String SITEMAP_COLUMN_RESOURCE_TYPE =
            "MetlifeApp/components/section/sitemap-category-column";
    /**
     * PATH_REF_PN.
     */
    public static final String PATH_REF_PN = "pathRef";
    /**
     * TYPE_PN.
     */
    public static final String TYPE_PN = "manualConfiguration";
    /**
     * PAGES_PN.
     */
    public static final String PAGES_PN = "pages";
    /**
     * LIST_PAGES_KEY.
     */
    public static final String LIST_PAGES_KEY = "list.pages";
    /**
     * LIST_MANUAL_PAGES_KEY.
     */
    public static final String LIST_MANUAL_PAGES_KEY = "list.manualPages";

    /**
     * IS_MANUAL_KEY.
     */
    public static final String IS_MANUAL_KEY = "isManual";

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.HIGHER_PRIORITY + 1;
    }

    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(CATEGORY_SUBNAV_RESOURCE_TYPE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        final Map<String, Object> configObject = (Map<String, Object>)
                contentModel
                .get(Constants.CONFIG_PROPERTIES_KEY);
        if (contentObject.containsKey(TYPE_PN)) {
            contentObject.put(IS_MANUAL_KEY, true);
            if (contentObject.containsKey(PAGES_PN)) {
                List<Map> items = new ArrayList<>();
                if (contentObject.get(PAGES_PN) instanceof ArrayList) {
                    items = JSONUtils.jsonStringObjectListToMapList(
                            (ArrayList<String>) contentObject.get(PAGES_PN));
                } else {
                    items.add(
                            JSONUtils.jsonStringObjectToMap(contentObject.get(
                                    PAGES_PN).toString()));
                }
                //TO DO: Prevent overrides from traversed list
                if (request.getResource().isResourceType(
                        SITEMAP_COLUMN_RESOURCE_TYPE)) {
                    contentModel.set(LIST_MANUAL_PAGES_KEY, items);
                } else {
                    contentModel.set(LIST_PAGES_KEY, items);
                }


            }
            contentObject.put(PATH_REF_PN, null);
        } else {
            if (configObject.containsKey(PATH_REF_PN)
                    && StringUtils.isNotBlank(configObject.get(PATH_REF_PN)
                    .toString())
                    && contentModel.has(configObject.get(PATH_REF_PN)
                    .toString())) {
                contentObject.put(PATH_REF_PN, contentModel.getAsString(
                        configObject.get(PATH_REF_PN).toString()));
            } else {
                final String basePath = contentModel.getAsString(Constants.PAGE
                                + Constants.DOT + Constants.PATH);

                final ResourceResolver resourceResolver = request.getResourceResolver();
                final Resource resourcePath = resourceResolver.getResource(basePath);

                if (resourcePath != null) {
                    contentObject.put(PATH_REF_PN, resourcePath.getParent().getPath());
                }
            }
        }
    }
}
