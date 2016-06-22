package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;

/**
 * Created by j.amorataya on 2/10/16.
 */
@Component
@Service
public class GetListFromPathContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {


    /**
     * JCR_TITLE.
     */
    private static final String JCR_TITLE = "jcr:title";
    /**
     * NAME_PROPERTY.
     */
    private static final String NAME_PROPERTY = "name";
    /**
     * PROPERTY_PATH.
     */
    private static final String PROPERTY_PATH = "mt_propertyPath";

    @Override
    public final void process(final SlingHttpServletRequest request,
                        final ContentModel contentModel) throws Exception {

        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                + PROPERTY_PATH)) {
            final ArrayList<String> properyList = (ArrayList<String>)
                    Utils.getPropertyAsList(contentModel,
                            Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                    + PROPERTY_PATH);
            for (String property : properyList) {
                if (contentModel.has(Constants.CONTENT + Constants.DOT
                        + property)) {
                    final String pathProperty = (String) contentModel.get(
                            Constants.CONTENT + Constants.DOT + property);

                    final ResourceResolver resourceResolver = request
                            .getResourceResolver();
                    final Resource resource = resourceResolver.getResource(
                            pathProperty);
                    final ArrayList<Object> list = new ArrayList<>();
                    if (resource != null) {
                        final Iterator<Resource> children =
                                resource.listChildren();
                        while (children.hasNext()) {
                            final Resource child = children.next();
                            final Map<String, Object> element =
                                    Maps.newHashMap();
                            element.put(NAME_PROPERTY, child.getName());
                            final ValueMap vmap = child.adaptTo(ValueMap.class);
                            if (vmap.containsKey(JCR_TITLE)) {
                                element.put(Constants.TITLE,
                                        vmap.get(JCR_TITLE));
                            }
                            list.add(element);


                        }
                    }

                    contentModel.set(Constants.CONTENT + Constants.DOT
                            + property, list);

                }
            }
        }

    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY;
    }


    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(
                MetLifeConstants.NEWS_ROOM_ARCHIVE_RESOURCE_TYPE);

    }
}
