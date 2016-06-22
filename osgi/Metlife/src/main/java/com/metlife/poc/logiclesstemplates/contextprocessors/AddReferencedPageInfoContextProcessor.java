package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xumak.base.assets.AssetPathService;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xumak.base.Constants;

/*
* AddReferencedPageInfoContextProcessor
* ---------------------------------------------------------------------------- *
* CHANGE HISTORY
* ----------------------------------------------------------------------------
* Version | Date       | Developer      | Changes
* 1.0     | 2015/03/11 | Pablo Alecio   | Initial Creation
* ---------------------------------------------------------------------------- *
*/

/**
 * AddReferencedPageInfoContextProcessor.
 */
@Component
@Service
public class AddReferencedPageInfoContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * REFERENCE_CONFIG_KEY_NAME.
     */
    public static final String REFERENCE_CONFIG_KEY_NAME =
            "referencePropertyName";
    /**
     * REFERENCE_DESTINATION_CONFIG_KEY_NAME.
     */
    public static final String REFERENCE_DESTINATION_CONFIG_KEY_NAME =
            "referenceDestinationPropertyName";
    /**
     * REFERENCE_KEY.
     */
    public static final String REFERENCE_KEY = "reference";

    /**
     * assetPathService.
     */
    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy =
            ReferencePolicy.DYNAMIC)
    private AssetPathService assetPathService;

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(REFERENCE_CONFIG_KEY_NAME);
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
    ContentModel contentModel)
            throws Exception {

        final Resource resource = request.getResource();
        final ResourceResolver resourceResolver =
                resource.getResourceResolver();
        final PageManager pageManager = resourceResolver.adaptTo(
                PageManager.class);
        final String referencePropertyName =
                contentModel.getAsString(Constants.CONFIG_PROPERTIES_KEY
                        + Constants.DOT
                        + REFERENCE_CONFIG_KEY_NAME);
        if (contentModel.has(referencePropertyName)) {
            final String referencePath = contentModel.getAsString(
                    referencePropertyName);
            final Page referencedPage = pageManager.getPage(referencePath);
            if (null != referencedPage) {
                final Map<String, Object> pagePropertiesMap = Maps.newHashMap();

                for (Map.Entry entry : referencedPage.getProperties()
                        .entrySet()) {
                    final Object value = entry.getValue();
                    if (value instanceof String[]) {
                        final List<String> list = Lists.newArrayList();
                        list.addAll(Arrays.asList(((String[]) value)).subList(
                                0, ((String[]) value).length));
                        pagePropertiesMap.put((String) entry.getKey(), list);
                    } else {
                        pagePropertiesMap.put((String) entry.getKey(), value);
                    }
                }
                //adding the page image path
                pagePropertiesMap.put(Constants.IMAGE_PATH, assetPathService
                        .getPageImagePath(referencedPage, resource));
                if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY
                        + Constants.DOT
                        + REFERENCE_DESTINATION_CONFIG_KEY_NAME)) {
                    final String destinationKeyName =
                            contentModel.getAsString(
                                    Constants.CONFIG_PROPERTIES_KEY
                                            + Constants.DOT
                                    + REFERENCE_DESTINATION_CONFIG_KEY_NAME
                            );
                    contentModel.set(destinationKeyName, pagePropertiesMap);

                } else {
                    contentModel.set(REFERENCE_KEY, pagePropertiesMap);
                }
            }
        }
    }
}
