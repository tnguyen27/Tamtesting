package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.xumak.base.Constants;



/**
 * AddItemPropertyBasedOnIndexContextProcessor
 * This context processor obtains the lists detailed in xk_addItemProperty
 * and add a new property, named using the xk_newPropertyName property.
 * The new property will be added to the items with an index bigger than the
 * defined in xk_startItemsIndexAt.
 *
 * --------------------------------------------------------------------------------------
 * CHANGE HISTORY
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer         | Changes
 * 1.0     | 04/22/2016 | Pablo García      | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component
@Service
public class AddItemPropertyBasedOnIndexContextProcessor extends
            AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * XK configuration to define the name of the list to set the new property.
     */
    public static final String ADD_PROPERTY_TO_LIST = "xk_addItemProperty";
    /**
     * XK configuration to define the index where the new property will begin.
     */
    public static final String START_AT = "xk_startItemsIndexAt";
    /**
     * XK configuration to define the name of the new property to add.
     */
    public static final String NEW_PROPERTY_NAME = "xk_newPropertyName";
    /**
     * Key to define where the row separator will be set.
     */
    public static final String ADD_SEPARATOR_KEY = "addSeparator";
    /**
     * Key to define if the current item of the list is even.
     */
    public static final String EVEN = "even";
    /**
     * Key to define if the current item of the list is odd.
     */
    public static final String ODD = "odd";

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public boolean mustExist() {
        return false;
    }

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY - 10; //must be executed after any other context processor.
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    protected XCQBConfigurationProvider configurationProvider;

    /**
     * Set the required property names to execute the Context Processor.
     * @return A hash set with the requiered properties.
     */
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(ADD_PROPERTY_TO_LIST);
    }

    /**
     * Obtain the list of 'lists' that we are going to add an index.
     * @param resource JCR resource.
     * @return A Collection of the lists that will be modified.
     * @throws Exception The exception to be thrown in case of an error.
     */
    public Collection<String>  getListsKeyNames(final Resource resource) throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(resource.getResourceType());
        final Collection<String> congifurationListsKeyNames =
                configuration.asStrings(ADD_PROPERTY_TO_LIST);
        return congifurationListsKeyNames;
    }

    /**
     * Obtain the number of the start index.
     * @param resource JCR resource.
     * @return A integer number, representing the start index for the lists.
     * @throws Exception The exception to be thrown in case of an error.
     */
    public int  startIndex(final Resource resource) throws Exception {
        int startIndexReturn;
        final XCQBConfiguration configuration = configurationProvider.getFor(resource.getResourceType());
        try {
            startIndexReturn = Integer.parseInt(configuration.asString(START_AT));
        } catch (Exception e) {
            startIndexReturn =  0;
        }
        return startIndexReturn;
    }

    /**
     * Obtain the name of the new property to be added.
     * @param resource JCR resource.
     * @return A string with the name of the new property.
     * @throws Exception The exception to be thrown in case of an error.
     */
    public String getNewPropertyName(final Resource resource) throws Exception {
        String propertyName;
        final XCQBConfiguration configuration = configurationProvider.getFor(resource.getResourceType());
        try {
            propertyName = configuration.asString(NEW_PROPERTY_NAME);
        } catch (Exception e) {
            propertyName =  "";
        }
        return propertyName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel) throws Exception {
        int index = 0;
        final Resource resource = request.getResource();
        final int startAt = startIndex(resource);
        final String newPropertyName = getNewPropertyName(resource);
        for (String listKeyName : getListsKeyNames(resource)) {
            final Object list = contentModel.get(listKeyName);
            if (list instanceof Collection) {
                final Collection collection = (Collection) list;
                int idx = startAt;
                for (Object item : collection) {
                    if (item instanceof Map) {
                        final Map<String, Object> currentItem = (Map<String, Object>) item;
                        if (index >= startAt) {
                            currentItem.put(newPropertyName, true);
                            idx++;
                            if (idx % 2 == 0) {
                                currentItem.put(EVEN, Constants.TRUE);
                            } else {
                                currentItem.put(ODD, Constants.TRUE);
                            }
                            if (index == startAt) {
                                currentItem.put(ADD_SEPARATOR_KEY, true);
                            }
                        }
                        index++;
                    }
                }
            }
        }
    }

}
