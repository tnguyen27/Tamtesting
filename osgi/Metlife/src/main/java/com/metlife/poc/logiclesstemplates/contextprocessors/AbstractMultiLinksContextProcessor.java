package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.Sets;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import com.xumak.base.util.GeneralRequestObjects;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * AbstractMultiLinksContextProcessor.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 01/14/2016 | jpol@xumak.com  | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Service
public class AbstractMultiLinksContextProcessor
        extends AbstractResourceTypeCheckContextProcessor<ContentModel> {

    /**
     * RESOURCE_TYPE.
     */
    public static final String RESOURCE_TYPE =
            "MetlifeApp/components/section/abstract-multilinks";
    /**
     * NUMBER_OF_ITEMS_KEY.
     */
    public static final String NUMBER_OF_ITEMS_KEY = "numberofitems";
    /**
     * LINK_TEXT_KEY.
     */
    public static final String LINK_TEXT_KEY = "linkText";
    /**
     * LINK_TYPE_KEY.
     */
    public static final String LINK_TYPE_KEY = "linkType";
    /**
     * INTERNAL_LINK_KEY.
     */
    public static final String INTERNAL_LINK_KEY = "internalLink";
    /**
     * EXTERNAL_LINK_KEY.
     */
    public static final String EXTERNAL_LINK_KEY = "externalLink";
    /**
     * TARGET_LINK_KEY.
     */
    public static final String TARGET_LINK_KEY = "targetLink";

    /**
     * INTERNAL.
     */
    public static final String INTERNAL = "internal";
    /**
     * EXTERNAL.
     */
    public static final String EXTERNAL = "external";
    /**
     * TITLE.
     */
    public static final String TITLE = "title";
    /**
     * LINK.
     */
    public static final String LINK = "link";
    /**
     * TARGET.
     */
    public static final String TARGET = "target";
    /**
     * HTML.
     */
    public static final String HTML = ".html";

    /**
     * Logger.
     */
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
            AbstractMultiLinksContextProcessor.class);

    /**
     * resourceResolverFactory.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(RESOURCE_TYPE);
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        final ResourceResolver resourceResolver = resourceResolverFactory
                .getAdministrativeResourceResolver(null);
        final Style style = GeneralRequestObjects.getCurrentStyle(request);
        final Resource resource = resourceResolver.getResource(style.getPath());
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);

        final ValueMap ressMap = ResourceUtil.getValueMap(resource);
        final String numberofitems = ressMap.get(NUMBER_OF_ITEMS_KEY,
                String.class);

        if (null != numberofitems) {

            final String[] linkTextList = ressMap.get(LINK_TEXT_KEY,
                    String[].class);
            final String[] linkTypeList = ressMap.get(LINK_TYPE_KEY,
                    String[].class);
            final String[] internalLinkList = ressMap.get(INTERNAL_LINK_KEY,
                    String[].class);
            final String[] externalLinkList = ressMap.get(EXTERNAL_LINK_KEY,
                    String[].class);
            final String[] targetLinkList = ressMap.get(TARGET_LINK_KEY,
                    String[].class);

            final int numberofitemsInt = Integer.parseInt(numberofitems);
            final ArrayList<HashMap> linksList = new ArrayList<HashMap>();

            for (int i = 0; i < numberofitemsInt; i++) {

                final HashMap<String, Object> linkMap =
                        new HashMap<String, Object>();
                final String title = getStringData(linkTextList, i);
                final String linkType = getStringData(linkTypeList, i);

                String link = "";
                if (linkType.equals(INTERNAL)) {
                    if (null != internalLinkList) {
                        link = getStringData(internalLinkList, i);
                        link = link.concat(HTML);
                    }
                } else if (linkType.equals(EXTERNAL)) {
                    if (null != externalLinkList) {
                        link = getStringData(externalLinkList, i);
                    }
                }

                final String target = getStringData(targetLinkList, i);
                linkMap.put(TITLE, title);
                linkMap.put(LINK, link);
                linkMap.put(TARGET, target);
                linksList.add(linkMap);

            }
            contentObject.put("linksListObj", linksList);
        }
    }

    /**
     * getStringData.
     * @param data The string array that contains the data
     * @param index the index to be found in the data array
     * @return data[index]
     */
    private String getStringData(final String[] data, final int index) {
        String getStringDataResult = " ";
        if (null != data) {
            getStringDataResult = data[index];
        }
        return getStringDataResult;
    }

    @Override
    public final boolean mustExist() {
        return false;
    }
}
