package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.metlife.poc.util.MetLifeConstants;

/**
 * Created by j.amorataya on 2/16/16.
 */
@Component
@Service
public class GetChildrenFromPathContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * PATH_TOCHILDREN.
     */
    private static final String PATH_TOCHILDREN = "xk_GetChildrenFromPath";
    /**
     * CHILDREN_LIST.
     */
    private static final String CHILDREN_LIST = "childrenList";

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(PATH_TOCHILDREN);
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
    public final void process(final SlingHttpServletRequest request,
                        final ContentModel contentModel) throws Exception {

        String basePath = contentModel.getAsString(Constants.PAGE
                + Constants.DOT + Constants.PATH);
        final String pathFromProperty = contentModel.getAsString(
                Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                        + PATH_TOCHILDREN); //desing
        // .basepath


        if (contentModel.has(pathFromProperty)) {
            basePath = contentModel.getAsString(pathFromProperty);
        }
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Resource baseResource = resourceResolver.getResource(basePath);
        if (baseResource != null) {
            final ArrayList<Object> childrenList = new ArrayList<>();

            final Iterator<Resource> children = baseResource.listChildren();
            while (children.hasNext()) {
                final Resource child = children.next();

                if (child.isResourceType(MetLifeConstants.CQ_PAGE)) {
                    final HashMap<String, Object> mapChild = new HashMap();
                    final ValueMap valueMapChild = child.getChild(
                            Constants.JCR_CONTENT)
                            .adaptTo(ValueMap.class);
                    final Iterator vmapIt = valueMapChild.entrySet().iterator();
                    while (vmapIt.hasNext()) {
                        final Map.Entry pair = (Map.Entry) vmapIt.next();
                        mapChild.put(pair.getKey().toString().replace(":",
                                ""), pair.getValue());

                    }

                    mapChild.put(Constants.NAME, child.getName());

                    childrenList.add(mapChild);
                }
            }
            contentModel.set(Constants.CONTENT + Constants.DOT + CHILDREN_LIST,
                    childrenList);

        }

    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY;
    }
}
