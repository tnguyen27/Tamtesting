package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.commons.Externalizer;
import com.google.common.collect.Lists;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * BreadcrumbSchemaContextProcessor.
 *
 * This context processor add breadcrumb information in content model with
 * absolute path of pages.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date        | Developer          | Changes
 * 1.0     | 7/6/2016    | Marco Cali         | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class BreadcrumbSchemaContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * Constants used in this class.
     */
    private static final String POSITION = "position";
    private static final String PATH_VALUE = "path";
    private static final String PAGE_TITLE = "navigationTitle";
    private static final String CANONICAL_URL_LIST = "canonicalUrlList";
    private static final String LIST_PAGES = "list.pages";

    /**
     * PRIORITY.
     */
    protected static final int PRIORITY = com.xumak.base.util.NumberUtils
            .nextPrime(Constants.MEDIUM_PRIORITY);
    /**
     * BREADCRUMB_SCHEMA_RESOURCE_TYPE.
     */
    private static final String BREADCRUMB_SCHEMA_RESOURCE_TYPE =
            "MetlifeApp/components/section/breadcrumb-schema";

    @Override
    public final String requiredResourceType() {
        return BREADCRUMB_SCHEMA_RESOURCE_TYPE;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {
        final Resource resource = request.getResource();
        final ResourceResolver resourceResolver = resource.getResourceResolver();

        //Object that contains information about externalizer path.
        final Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        final Collection<Map<String, Object>> externalizerList = Lists.newArrayList();
        if (contentModel.has(LIST_PAGES)) {
            final ArrayList listPages = (ArrayList) contentModel.get(LIST_PAGES);

            //Building list of paths as external links.
            for (int i = 0; i < listPages.size(); i++) {
                final Map pageElement = (HashMap) listPages.get(i);
                if (pageElement.containsKey(PATH_VALUE) && pageElement.containsKey(PAGE_TITLE)) {

                    //Getting path as external link format.
                    final String canonicalUrl = externalizer.absoluteLink(request,
                            request.getScheme(), pageElement.get(PATH_VALUE).toString());

                    final Map<String, Object> listItem = new HashMap<String, Object>();
                    listItem.put(POSITION, i + 1);
                    listItem.put(PATH_VALUE, canonicalUrl);
                    listItem.put(PAGE_TITLE, pageElement.get(PAGE_TITLE).toString());
                    externalizerList.add(listItem);
                }
            }
        }
        contentModel.set(CANONICAL_URL_LIST, externalizerList);
    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return PRIORITY;
    }
}
