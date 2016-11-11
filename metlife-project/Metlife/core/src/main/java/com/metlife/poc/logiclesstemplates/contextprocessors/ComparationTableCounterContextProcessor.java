package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Sets;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.xumak.base.Constants;


/**
 * Created by jpol on 2/1/16.
 */
@Component
@Service
public class ComparationTableCounterContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {

    /**
     * RESOURCE_TYPE.
     */
    public static final String RESOURCE_TYPE =
            "MetlifeApp/components/section/product-comparison-table";
    /**
     * LIST_PAGES_KEY.
     */
    public static final String LIST_PAGES_KEY = "list.pages";
    /**
     * COUNTER_KEY.
     */
    public static final String COUNTER_KEY = "counter";

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(RESOURCE_TYPE);
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        final ArrayList<Object> pages = (ArrayList<Object>) contentModel.get(
                LIST_PAGES_KEY);

        final ArrayList<Object> pagesWithCounter = new ArrayList<Object>();
        if (null != pages) {
            for (int i = 0; i < pages.size(); i++) {
                final int counter = i + 1;
                final  Map<String, Object> page = (Map<String, Object>)
                        pages.get(i);
                page.put(COUNTER_KEY, counter);
                pagesWithCounter.add(page);
            }
        }

        contentObject.put(LIST_PAGES_KEY, pagesWithCounter);
    }

}
