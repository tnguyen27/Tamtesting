package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.commons.jcr.JcrUtil;
import com.metlife.poc.util.JSONUtils;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import com.xumak.base.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This context processor add a valid JCR ID for each category of category name.
 * ----------------------------------------------------------------------------
 *
 * CHANGE HISTORY
 * ----------------------------------------------------------------------------
 * Version  | Date          | Developer          | Changes
 * 1.0      | 2/18/16       | Allan Revolorio    | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddCategoriesIDContextProcessor extends
        AbstractResourceTypeCheckContextProcessor {

    /**
     * CONTACT_RESULTS_CONTAINER.
     */
    public static final String CONTACT_RESULTS_CONTAINER =
            "MetlifeApp/components/section/contactus-results-container";
    /**
     * CATEGORIES_KEY.
     */
    public static final String CATEGORIES_KEY = "categories";
    /**
     * CATEGORY_LIST.
     */
    public static final String CATEGORY_LIST = "categoryList";
    /**
     * CATEGORY_ID_LIST.
     */
    public static final String CATEGORY_ID_LIST = "categoryIDList";
    /**
     * PREFIX.
     */
    public static final String PREFIX = "contact-results-";


    /**
     * requiredResourceType.
     * @return String
     */
    public final String requiredResourceType() {
        return CONTACT_RESULTS_CONTAINER;
    }

    @Override
    public final void process(final SlingHttpServletRequest request,
                        final ContentModel contentModel) throws Exception {
        if (contentModel.has(Constants.CONTENT + Constants.DOT
                + CATEGORIES_KEY)) {
            if (contentModel.get(Constants.CONTENT + Constants.DOT
                    + CATEGORIES_KEY)
                    instanceof List) {
                final ArrayList categoriesRaw = (ArrayList) contentModel.get(
                        Constants.CONTENT + Constants.DOT + CATEGORIES_KEY);
                final List<Map> categories = JSONUtils
                        .jsonStringObjectListToMapList(categoriesRaw);
                if (!categories.isEmpty()) {
                    final ArrayList<Map<String, Object>> categoryList = new
                            ArrayList<>();
                    final ArrayList<String> categoryIDList = new ArrayList<>();
                    for (Map category : categories) {
                        if (category.containsKey(Constants.NAME)) {
                            if (category.get(Constants.NAME)
                                    instanceof String) {
                                final String categoryName = (String)
                                        category.get(Constants.NAME);
                                final String id = JcrUtil.createValidName(
                                        PREFIX + categoryName);
                                if (StringUtils.isNotEmpty(id)) {
                                    category.put(Constants.ID, id);
                                    categoryIDList.add(id);
                                    categoryList.add(category);
                                }
                            }
                        }
                    }
                    if (!categoryList.isEmpty()) {
                        contentModel.set(Constants.CONTENT + Constants.DOT
                                + CATEGORY_LIST, categoryList);
                        contentModel.set(Constants.CONTENT + Constants.DOT
                                + CATEGORY_ID_LIST, categoryIDList);
                    }
                }
            }
        }
    }
}
