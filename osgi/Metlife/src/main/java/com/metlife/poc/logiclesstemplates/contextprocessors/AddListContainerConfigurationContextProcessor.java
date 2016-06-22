package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Sets;
import com.xumak.base.configuration.Mode;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.xumak.base.Constants;

/**
 * AddListContainerConfigurationContextProcessor
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/01/27 | J. Alejandro Morataya | Initial Creation
 * ----------------------------------------------------------------------------
 * use xk_addListProperty for single properties, ex <context>.<propertyName>
 * use xk_addListPropertyArray for array of properties:
 * ex:     <context>.<propertyArrayName>:<elementOfArrayName>
 */

@Component
@Service
public class AddListContainerConfigurationContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * MEDIA_CONTACT_RESOURCE_TYPE.
     */
    private static final String MEDIA_CONTACT_RESOURCE_TYPE =
            "MetlifeApp/components/section/media-contact";

    /**
     * XK_ADD_LIST_PROPERTY.
     */
    private static final String XK_ADD_LIST_PROPERTY = "xk_addListProperty";
    /**
     * XK_ADD_LIST_PROPERTY_ARRAY.
     */
    private static final String XK_ADD_LIST_PROPERTY_ARRAY =
            "xk_addListPropertyArray";
    /**
     * ITEMS_KEY.
     */
    private static final String ITEMS_KEY = "items";
    /**
     * COLON.
     */
    private static final String COLON = ":";


    /**
     * Returns the required resource types.
     * @return Set of strings.
     */
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(MEDIA_CONTACT_RESOURCE_TYPE);
    }

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;


    @Override
    public final void process(
            final SlingHttpServletRequest slingHttpServletRequest,
            final TemplateContentModel templateContentModel)
            throws Exception {

        final XCQBConfiguration configuration =
                configurationProvider.getFor(slingHttpServletRequest
                        .getResource().getResourceType());

        final Collection<String> propsWithList = configuration.asStrings(
                XK_ADD_LIST_PROPERTY, Mode.MERGE);
        for (String propName : propsWithList) {

            if (templateContentModel.has(propName)) {
                int item = 0;
                if (templateContentModel.get(propName) instanceof String) {
                    final String itemString = templateContentModel.getAsString(
                            propName);
                    item = Integer.valueOf(itemString);
                } else {
                    item = (int) templateContentModel.get(propName);
                }
                final String listName = propName.substring(propName.indexOf(
                        Constants.DOT) + 1);
                templateContentModel.set(propName, getItemList(item, listName
                        + Constants.DASH + ITEMS_KEY));
            }

        }

        final Collection<String> propsWithListArray = configuration.asStrings(
                XK_ADD_LIST_PROPERTY_ARRAY, Mode.MERGE);
        for (String propName : propsWithListArray) {
            final String nameArray = propName.substring(0, propName.indexOf(
                    COLON));
            final String arrayElement = propName.substring(propName.lastIndexOf(
                    COLON) + 1, propName.length());

            if (templateContentModel.has(nameArray)) {
                final  ArrayList<HashMap<String, Object>> categories =
                        (ArrayList) templateContentModel.get(nameArray);
                int y = 0;
                if (categories != null) {
                    for (HashMap<String, Object> category : categories) {
                        final int item = (int) category.get(arrayElement);
                        final String nameList = nameArray.substring(nameArray
                                .lastIndexOf(Constants.DOT) + 1,
                                nameArray.length()) + Constants.DASH
                                + ITEMS_KEY;

                        category.put(nameList,
                                getItemList(item, nameList + Constants.DASH
                                        + y));
                        y++;
                    }
                }
            }

        }
    }

    /**
     * Private method to get the item list.
     * @param itemNumber The item number.
     * @param propName The property name.
     * @return ArrayList of Strings.
     */
    private ArrayList<String> getItemList(final int itemNumber,
                                          final String propName) {
        final ArrayList<String> items = new ArrayList<String>();
        for (int x = 0; x < itemNumber; x++) {
            items.add(propName + Constants.DASH + x);
        }
        return items;

    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 1;
    } //must be executed after AddDeserializedJsonListContextProcessor


}
