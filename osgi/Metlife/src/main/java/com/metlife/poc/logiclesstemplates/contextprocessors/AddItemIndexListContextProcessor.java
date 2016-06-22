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

/**.
 * ----------------------------------------------------------------------------
 * CHANGE HISTORY
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer         | Changes
 * 1.0     | 02/02/2016 | Jorge Hernández   | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddItemIndexListContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * ADD_INDEX_TO_LIST_PROPERTY_NAMES.
     */
    public static final String ADD_INDEX_TO_LIST_PROPERTY_NAMES =
            "xk_addItemIndex";
    /**
     * START_AT.
     */
    public static final String START_AT = "xk_startItemsIndexAt";
    /**
     * INDEX_KEY.
     */
    public static final String INDEX_KEY = "index";
    /**
     * EVEN.
     */
    public static final String EVEN = "even";
    /**
     * ODD.
     */
    public static final String ODD = "odd";

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        final int ten = 10;
        return Constants.LOW_PRIORITY - ten;
        //must be executed after any other context
        // processor
    }

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Returns the required properties names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(ADD_INDEX_TO_LIST_PROPERTY_NAMES);
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    /**
     * Obtain the list of 'lists' that we are going to add an index.
     *
     * @param resource The resource.
     * @return Collection of strings.
     * @throws Exception throws exception
     */
    public final Collection<String> getListsKeyNames(final Resource resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final Collection<String> congifurationListsKeyNames =
                configuration.asStrings(ADD_INDEX_TO_LIST_PROPERTY_NAMES);
        return congifurationListsKeyNames;
    }

    /**
     * Returns int.
     * @param resource The resource.
     * @return start index as int
     * @throws Exception throws exception
     */
    public final int startIndex(final Resource resource) throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        try {
            return Integer.parseInt(configuration.asString(START_AT));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Process.
     * @param request The SlingHttpServletRequest.
     * @param contentModel The conten model.
     * @throws Exception throws exception.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {
        final Resource resource = request.getResource();
        final int startAt = startIndex(resource);
        for (String listKeyName : getListsKeyNames(resource)) {
            final Object list = contentModel.get(listKeyName);
            if (list instanceof Collection) {
                final Collection collection = (Collection) list;
                int idx = startAt;
                for (Object item : collection) {
                    if (item instanceof Map) {
                        final Map<String, Object> currentItem =
                                (Map<String, Object>) item;
                        currentItem.put(INDEX_KEY, idx++);
                        if (idx % 2 == 0) {
                            currentItem.put(EVEN, Constants.TRUE);
                        } else {
                            currentItem.put(ODD, Constants.TRUE);
                        }
                    }
                }
            }
        }
    }

}
