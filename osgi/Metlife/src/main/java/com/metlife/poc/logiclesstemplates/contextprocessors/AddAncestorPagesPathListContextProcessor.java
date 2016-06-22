package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collection;

/**
 * AddAncestorPagesPathListContextProcessor class.
 */
@Component
@Service
public class AddAncestorPagesPathListContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * PRIORITY.
     */
    protected static final int PRIORITY = com.xumak.base.util.NumberUtils
            .nextPrime(Constants.HIGHER_PRIORITY - 20);
    /**
     * BREADCRUMB_RESOURCE_TYPE.
     */
    public static final String BREADCRUMB_RESOURCE_TYPE =
            "MetlifeApp/components/section/bread-crumb";
    /**
     * LEVEL_KEY.
     */
    public static final String LEVEL_KEY = "level";
    /**
     * PATHS_LIST_CONTEXT_KEY.
     */
    public static final String PATHS_LIST_CONTEXT_KEY = "list.paths";

    @Override
    public final String requiredResourceType() {
        return BREADCRUMB_RESOURCE_TYPE;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {
        final Resource resource = request.getResource();
        final ResourceResolver resourceResolver =
                resource.getResourceResolver();
        final PageManager pageManager =
                resourceResolver.adaptTo(PageManager.class);
        final Page page = pageManager.getContainingPage(resource);
        final String levelString = contentModel.getAsString(
                Constants.DESIGN_PROPERTIES_KEY + Constants.DOT + LEVEL_KEY);
        final int level = NumberUtils.toInt(levelString, 2);
        final int pageDepth = page.getDepth();
        final Collection<String> ancestorPagePaths = Lists.newArrayList();
        if (level < pageDepth) {
            for (int i = level; i < pageDepth; i++) {
                final Page ancestorPage = page.getAbsoluteParent(i);
                if (ancestorPage != null) {
                    final ValueMap currentPageProperties =
                            ancestorPage.getProperties();
                    if (!currentPageProperties.
                            containsKey(Constants.HIDE_IN_NAV)) {
                        ancestorPagePaths.add(ancestorPage.getPath());
                    }
                }
            }
        }
        contentModel.set(PATHS_LIST_CONTEXT_KEY, ancestorPagePaths);
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
