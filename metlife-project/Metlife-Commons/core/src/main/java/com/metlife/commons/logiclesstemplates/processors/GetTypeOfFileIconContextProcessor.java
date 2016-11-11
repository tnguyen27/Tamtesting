package com.metlife.commons.logiclesstemplates.processors;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.commons.util.ContextProcessorUtils;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**.
 * GetTypeOfFileIconContextProcessor
 * This context processor will get a file and a list path from the
 * configured properties and will add the path to the corresponding
 * icon to the file type. The icons will be configured in the list.
 *
 * Example:
 * xk_getIconForTypeOfFile: pdflink::fileReferences
 * xk_getIconForTypeOfFileList: iconsList
 *
 * Where
 * pdflink::fileReferences is the path to a file in the dam
 * AND
 * iconsList is a reference to a generic list
 *
 * Then
 * the context processor will add the content.pdflinkIcon to the
 * content model
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/07/19 | Diego Tello     | Initial creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class GetTypeOfFileIconContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * MULTI_FILES_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String MULTI_FILES_CONFIGURATION_PROPERTY_NAME =
            "xk_getMultiIconForTypeOfFile";

    /**
     * FILE_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String FILE_CONFIGURATION_PROPERTY_NAME =
            "xk_getIconForTypeOfFile";

    /**
     * LIST_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String LIST_CONFIGURATION_PROPERTY_NAME =
            "xk_getIconForTypeOfFileList";

    /**
     * ICON.
     * Constant String to concatenate to the content property
     */
    private static final String ICON = "Icon";

    /**
     * ILLEGAL_FORMAT_EXCEPTION.
     * This is the message that will be shown in the exception if the configured value does not
     * comply with the required format, ex: "content.categories::categoryName"
     */
    private static final String ILLEGAL_FORMAT_EXCEPTION =
            "The " + FILE_CONFIGURATION_PROPERTY_NAME + " value does not have the required format.";

    /**
     * ICON FILE.
     * Constant String to concatenate to the content property
     */
    private static final String ICON_FILE = "iconFile";

    /**
     * Logger.
     */
    //private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
    //        GetTypeOfFileIconContextProcessor.class);

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY;
    }


    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(FILE_CONFIGURATION_PROPERTY_NAME, LIST_CONFIGURATION_PROPERTY_NAME);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final Collection<String> configuredFilePathLocations = ContextProcessorUtils.getPathRefListKeyNames(
                configurationProvider, slingHttpServletRequest.getResource(), FILE_CONFIGURATION_PROPERTY_NAME);
        final XCQBConfiguration configuration = configurationProvider.getFor(
                slingHttpServletRequest.getResource().getResourceType());
        final String configuredIconListPath = configuration.asString(LIST_CONFIGURATION_PROPERTY_NAME);
        final PageManager pageManager = slingHttpServletRequest.getResourceResolver().adaptTo(PageManager.class);
        final Page listPage = pageManager.getPage(configuredIconListPath);
        if (listPage != null) {
            final GenericList iconsGenericList = listPage.adaptTo(GenericList.class);
            final List<GenericList.Item> iconsList = iconsGenericList.getItems();
            for (final String filePathLocation : configuredFilePathLocations) {
                final String[] filePathParts = filePathLocation.split(MetLifeConstants.DOUBLE_COLON);
                if (filePathParts.length == 2) {
                    final Object file = ContextProcessorUtils.getFileFromDam(slingHttpServletRequest, filePathParts);
                    if (file != null) {
                        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + MULTI_FILES_CONFIGURATION_PROPERTY_NAME)) {
                            final String listName = configuration.asString(MULTI_FILES_CONFIGURATION_PROPERTY_NAME);
                            final Collection<Object> listFiles = Utils.getPropertyAsList(contentModel,
                                    Constants.CONTENT + Constants.DOT + listName);
                            Asset[] assetFiles = new Asset[configuredFilePathLocations.size()];
                            if (file instanceof Asset) {
                                final Asset assetsFiles = (Asset) file;
                                assetFiles[0] = assetsFiles;
                            } else if (file instanceof Asset[]) {
                                assetFiles = (Asset[]) file;
                            }
                            int count = 0;
                            final Iterator<Object> keys = listFiles.iterator();
                            while (keys.hasNext()) {
                                final Object key = keys.next();
                                final Map<String, Object> currentItem = (Map<String, Object>) key;
                                if (count < assetFiles.length && assetFiles[count] != null) {

                                    final String iconName = findImageNameFromIconsList(iconsList,
                                            assetFiles[count].getName());
                                    currentItem.put(ICON_FILE, iconName);

                                    count++;
                                }
                            }
                        } else {
                            if (file instanceof Asset) {
                                final Asset assetFile = (Asset) file;
                                final String iconName = findImageNameFromIconsList(iconsList, assetFile.getName());
                                contentModel.set(Constants.CONTENT + Constants.DOT + filePathParts[0] + ICON, iconName);
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException(ILLEGAL_FORMAT_EXCEPTION);
                }
            }
        }
    }

    /**
     * Looks for the icon file name for certain type of file.
     *
     * @param iconsList the list with the configured icons
     * @param fileName name of file
     * @return the file name of the icon
     */
    public static String findImageNameFromIconsList(final List<GenericList.Item> iconsList, final String fileName) {
        String iconName = Constants.BLANK;
        for (final GenericList.Item item:iconsList) {
            if (fileName.endsWith(Constants.DOT + item.getTitle())) {
                iconName = item.getValue();
                break;
            }
        }
        return iconName;
    }
}
