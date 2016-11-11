package com.metlife.commons.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.commons.util.FormsLibraryConstants;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
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
import org.apache.sling.api.resource.ValueMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**.
 * GetCategoriesFromDamFolderPathContextProcessor
 *
 * This context processor must receive a dam path pointing to a folder
 * where the forms reside and it will loop through the structure searching
 * for the direct children that have a resource type equals to sling:OrderedFolder,
 * each child that complies with this will be added to the resultant list and will contain
 * categoryName, categoryValue, categoryTitle and categoryOther (you can find the description
 * for each one of these in the declaration of the corresponding constant) for each
 * category. The resultant list will be stored in content.categories.
 *
 * Example:
 * xk_formsDirectory: content.formsPath
 *
 * Where
 * formsPath stores the path to the parent folder of the structure
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/08/23 | Diego Tello     | Initial creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class GetCategoriesFromDamFolderPathContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * FORMS_DIRECTORY_PROPERTY_NAME.
     * This is the name of the property that must be configured
     * to execute this content processor, it must point to a
     * folder in the dam.
     */
    public static final String FORMS_DIRECTORY_PROPERTY_NAME =
            "xk_formsDirectory";
    /**
     * CATEGORIES.
     * Name of the content model property where the
     * resultant list will be stored.
     */
    public static final String CATEGORIES = "content.categories";
    /**
     * CATEGORY_NAME.
     * Key for each category name, the category name will be
     * the same as the jcr:title of the folder.
     */
    private static final String CATEGORY_NAME = "categoryName";
    /**
     * CATEGORY_VALUE.
     * Key for each category value, the category value will be
     * the same as the node name, the node being the sling:OrderedFolder
     * resource.
     */
    private static final String CATEGORY_VALUE = "categoryValue";
    /**
     * CATEGORY_TITLE.
     * Key for each category title, same as CATEGORY_VALUE but will
     * contain a _title suffix, useful if you need an id or class to
     * differentiate category divs of included components.
     */
    private static final String CATEGORY_TITLE = "categoryTitle";
    /**
     * CATEGORY_OTHER.
     * Key for each category other, same as CATEGORY_VALUE but will
     * contain a _other suffix, useful if you need an id or class to
     * differentiate category divs of included components.
     */
    private static final String CATEGORY_OTHER = "categoryOther";
    /**
     * TITLE_SUFFIX.
     * Suffix to append to the CATEGORY_VALUE.
     */
    private static final String TITLE_SUFFIX = "_title";
    /**
     * OTHER_SUFFIX.
     * Suffix to append to the CATEGORY_VALUE.
     */
    private static final String OTHER_SUFFIX = "_other";
    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Logger.
     */
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
            GetCategoriesFromDamFolderPathContextProcessor.class);

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(FORMS_DIRECTORY_PROPERTY_NAME);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final Resource resource = slingHttpServletRequest.getResource();
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final String folderPathProperty = configuration.asString(FORMS_DIRECTORY_PROPERTY_NAME);
        final String folderPath = contentModel.getAsString(folderPathProperty);
        final List<Map<String, Object>> categories = new LinkedList();
        if (folderPath != null && !folderPath.equals(Constants.BLANK)) {
            final Resource formsDir = slingHttpServletRequest.getResourceResolver().getResource(folderPath);
            if (formsDir != null) {
                final Iterator<Resource> children = formsDir.listChildren();
                Resource child = null;
                while (children.hasNext()) {
                    child = children.next();
                    if (child.getResourceType().equals(FormsLibraryConstants.SLING_ORDERED_FOLDER)) {
                        final Map<String, Object> categoryWrapper = Maps.newHashMap();
                        final Resource childJcrContent = child.getChild(
                                Constants.JCR_CONTENT);
                        if (childJcrContent != null) {
                            final ValueMap childMap = childJcrContent.adaptTo(
                                    ValueMap.class);
                            if (childMap != null && childMap.containsKey(FormsLibraryConstants.JCR_TITLE)) {
                                categoryWrapper.put(CATEGORY_NAME, (String) childMap.get(
                                        FormsLibraryConstants.JCR_TITLE));
                                categoryWrapper.put(CATEGORY_VALUE, child.getName());
                                categoryWrapper.put(CATEGORY_TITLE, child.getName() + TITLE_SUFFIX);
                                categoryWrapper.put(CATEGORY_OTHER, child.getName() + OTHER_SUFFIX);
                                categories.add(categoryWrapper);
                            } else {
                                logger.error("The childMap is null or does not contain a jcr:title");
                            }
                        } else {
                            logger.error("The childJcrContent is null");
                        }
                    }
                }
                contentModel.set(CATEGORIES, categories);
            } else {
                logger.error("The formsDir is null");
            }
        } else {
            logger.error("The folderPath is null or empty");
        }
    }
}
