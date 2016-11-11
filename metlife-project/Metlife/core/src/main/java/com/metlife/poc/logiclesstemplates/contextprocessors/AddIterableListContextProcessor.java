package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.xumak.base.Constants;

/**
 * AddIterableListContextProcessor
 * Add an iterated list with unique names for each element.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 03/05/2015 | Gabriel Orozco  | Initial Creation
 * 1.1     | 05/26/2016 | Lesly Quinonez  | Adapt to Metlife project.
 * --------------------------------------------------------------------------------------
 */

@Component
@Service
public class AddIterableListContextProcessor
        extends AbstractConfigurationContextProcessor {

    private static final String COUNT_PROPERTIES = "xk_iterableListCountProperties";
    private static final String ITEMS_NAME = "xk_iterableListItemsName";
    private static final String ITERABLE_LIST_SUFFIX = "Iteration";
    private static final String DEFAULT_ITEMS_NAME = "item";

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(COUNT_PROPERTIES);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    protected boolean mustExist() {
        return false;
    }

    @Override
    public void process(final SlingHttpServletRequest request,
                        final ContentModel contentModel) throws Exception {
        //getting count properties from xk.config
        final List<String> countProperties = (List) Utils.getPropertyAsList(contentModel,
                Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + COUNT_PROPERTIES);
        //getting items name from xk.config
        final List<String> itemsName = (List) Utils.getPropertyAsList(contentModel,
                Constants.CONFIG_PROPERTIES_KEY + Constants.DOT + ITEMS_NAME);
        if (null != countProperties) {
            int itemsNameIndex = 0;
            //for each count property, we will get it's value and create an iterable list with that size
            for (final String countProperty:countProperties) {
                if (StringUtils.isNotBlank(countProperty)) {
                    //getting property's value (count)
                    final int count = NumberUtils.toInt(contentModel.getAsString(countProperty), 0);
                    //creating the iterable list
                    final List list = Lists.newArrayList();
                    if (count > 0) {
                        for (int i = 0; i < count; i++) {
                            //setting items name, if there isn't configured an item name for the count property
                            //the default name 'item' it is used
                            String itemName;
                            if (itemsNameIndex < itemsName.size()) {
                                itemName = itemsName.get(itemsNameIndex);
                            } else {
                                itemName = DEFAULT_ITEMS_NAME;
                            }
                            list.add(itemName + i);
                        }
                    }
                    //adding iterable list, this will be available in the view using the
                    // <COUNT_PROPERTY_NAME_AND_CONTEXT> + <ITERABLE_LIST_SUFFIX>.
                    // For example: content.myCountPropertyIteration
                    contentModel.set(countProperty + ITERABLE_LIST_SUFFIX, list);
                }
                itemsNameIndex++; //next item name
            }
        }
    }
}
