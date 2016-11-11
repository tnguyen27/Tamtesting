package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xumak.base.Constants;
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
import java.util.Map;
import java.util.Set;

/**.
 * ----------------------------------------------------------------------------
 * CHANGE HISTORY
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer         | Changes
 * 1.0     | 11/05/2016 | Pablo García      | Initial Creation
 * ----------------------------------------------------------------------------
 * The xk_addIdToElements propety of the xk.config needs to have this structure:
 * <contextName.propertyName>::<propertyName> or
 * <contextName.propertyName>
 * it depends if you need to add IDs to a property of each element of a list (first case)
 * or if you need to add IDs to each element of a list, or to a single element. (second case)
 * e.g.  global.mainLinks::links
 * with the config above, the CP will add IDs to each element of the list named link.
 * e.g. global.mainLinks
 * with the config above, the CP will add IDs to each element of the list named mainLinks
 * e.g. global.someLink
 * with the config above, the CP will add an ID for the property named someLink
 */

@Component
@Service
public class AddElementIDContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * XK configuration to define if the component will be managed by the CP.
     */
    public static final String ADD_ID_TO_ELEMENTS = "xk_addIdToElements";
    /**
     * Name of the property (element id) to be added to the map.
     */
    public static final String ELEMENT_ID = "elementId";

    /**
     * DOUBLE_COLON.
     */
    public static final String DOUBLE_COLON = "::";

    /**
     * UNDERSCORE.
     */
    public static final String UNDERSCORE = "_";

    /**
     * GLOBAL.
     */
    public static final String GLOBAL = "global";

    /**
     * Name of the current Node.
     */
    public static final String CURRENT_NODE_NAME_KEY = "content.name";


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
        return Constants.LOW_PRIORITY - 20; //must be executed after any other context processor.
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    protected XCQBConfigurationProvider configurationProvider;

    /**
     * Set the required property names to execute the Context Processor.
     * @return A hash set with the requiered properties.
     */
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(ADD_ID_TO_ELEMENTS);
    }

    /**
     * Obtain the list of elements that we are going to add an ID.
     * @param resource JCR resource.
     * @return A Collection of the elements that will be modified.
     * @throws Exception The exception to be thrown in case of an error.
     */
    public Collection<String> getListsKeyNames(final Resource resource) throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(resource.getResourceType());
        final Collection<String> congifurationListsKeyNames =
                configuration.asStrings(ADD_ID_TO_ELEMENTS);
        return congifurationListsKeyNames;
    }

    /**
     * Build the element id based on the page name, component name and element name.
     * @param resourceResolver JCR resourceResolver.
     * @param resource JCR resource.
     * @param contentModel content model.
     * @param elementName Name of the element
     * @return A string with the name of the ID.
     * @throws Exception The exception to be thrown in case of an error.
     */
    public String buildElementId(final ResourceResolver resourceResolver,
                                 final Resource resource,
                                 final ContentModel contentModel,
                                 final String elementName) throws Exception {
        final Object nodeName = contentModel.get(CURRENT_NODE_NAME_KEY);
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        final Page currentPage = pageManager.getContainingPage(resource);

        String propertyName = "";
        try {
            propertyName = new StringBuilder(propertyName).append(currentPage.getName())
                    .append(UNDERSCORE).append(nodeName.toString()).append(UNDERSCORE).append(elementName).toString();
            propertyName = propertyName.replaceAll("\\s+", "");
            propertyName = propertyName.replaceAll("\\.+", "-");
            propertyName = propertyName.replaceAll("\\:+", "");
        } catch (Exception e) {
            propertyName =  "";
        }
        return propertyName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel) throws Exception {
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Resource resource = request.getResource();
        for (final String elementKeyName : getListsKeyNames(resource)) {
            final String[] elementKeyNameArray = elementKeyName.split(DOUBLE_COLON);
            if (elementKeyNameArray.length == 2) {
                final String contentModelListPropertyName = elementKeyNameArray[0];
                final String contentModelPropertyName = elementKeyNameArray[1];
                final Object element = contentModel.get(contentModelListPropertyName);

                if (element instanceof Collection) {
                    final Collection collection = (Collection) element;
                    Integer outerCount = 1;
                    for (final Object item : collection) {
                        if (item instanceof Map) {
                            final Map<String, Object> currentListItem = (Map<String, Object>) item;

                            for (final Map.Entry<String, Object> currentMapItem : currentListItem.entrySet()) {
                                if (currentMapItem.getKey().equals(contentModelPropertyName)
                                        && currentMapItem.getValue() instanceof Collection) {
                                    final Collection itemCollection = (Collection) currentMapItem.getValue();
                                    Integer count = 1;
                                    for (final Object listItem : itemCollection) {
                                        if (listItem instanceof Map) {
                                            final Map<String, Object> currentItem = (Map<String, Object>) listItem;
                                            final String elementId = buildElementId(resourceResolver,
                                                    resource,
                                                    contentModel,
                                                    elementKeyName);
                                            currentItem.put(ELEMENT_ID,
                                                            elementId
                                                            + outerCount.toString()
                                                            + count.toString());
                                            count++;
                                        }
                                    }
                                }

                            }

                        }
                        outerCount++;
                    }

                }
            } else {
                final Object element = contentModel.get(elementKeyName);
                if (element instanceof Collection) {
                    final Collection collection = (Collection) element;
                    Integer count = 1;
                    for (final Object item : collection) {
                        if (item instanceof Map) {
                            final Map<String, Object> currentItem = (Map<String, Object>) item;
                            final String elementId = buildElementId(resourceResolver,
                                    resource,
                                    contentModel,
                                    elementKeyName);
                            currentItem.put(ELEMENT_ID, elementId + count.toString());
                            count++;
                        }
                    }
                } else {
                    final String elementId = buildElementId(resourceResolver, resource, contentModel, elementKeyName);
                    contentModel.set(elementKeyName + "Id", elementId);
                }
            }
        }
    }

}
