package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**.
 * ----------------------------------------------------------------------------
 * CHANGE HISTORY
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer         | Changes
 * 1.0     | 03/02/2016 | Jorge Hernández   | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddGenericListContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * ADD_GENERIC_LIST_PROPERTY_NAMES.
     */
    public static final String ADD_GENERIC_LIST_PROPERTY_NAMES =
            "xk_addGenericList";
    /**
     * TITLE_KEY.
     */
    public static final String TITLE_KEY = "title";
    /**
     * VALUE_KEY.
     */
    public static final String VALUE_KEY = "value";

    @Override
    public final boolean mustExist() {
        return false;
    }

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Returns the required properties names.
     * @return Set of strings with the names
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(ADD_GENERIC_LIST_PROPERTY_NAMES);
    }

    /**
     * Obtain the list of referenced paths that will be added to the context.
     *
     * @param resource The resource.
     * @return Collection of key names.
     * @throws Exception throws exception
     */
    public final Collection<String> getListsKeyNames(final Resource resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final Collection<String> congifurationListsKeyNames =
                configuration.asStrings(ADD_GENERIC_LIST_PROPERTY_NAMES);
        return congifurationListsKeyNames;
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
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {
        final ResourceResolver resourceResolver = request.getResource()
                .getResourceResolver();
        final PageManager pageManager = resourceResolver.adaptTo(
                PageManager.class);
        for (String listKeyName : getListsKeyNames(request.getResource())) {
            final String pathRefToList = contentModel.getAsString(listKeyName);
            try {
                final Page listPage = pageManager.getPage(pathRefToList);
                if (listPage != null) {
                    final GenericList list = listPage.adaptTo(
                            GenericList.class);
                    final LinkedList<Map> items = new LinkedList<>();
                    for (GenericList.Item genericItem : list.getItems()) {
                        final HashMap<String, String> item = new HashMap<>();
                        item.put(TITLE_KEY, genericItem.getTitle());
                        item.put(VALUE_KEY, genericItem.getValue().trim());
                        items.add(item);
                    }
                    contentModel.set(listKeyName, items);
                }
            } catch (Exception e) {
                log.error("Error getting Generic List [key: {}]  [path: {}]",
                        listKeyName, pathRefToList);
            }
        }
    }
}
