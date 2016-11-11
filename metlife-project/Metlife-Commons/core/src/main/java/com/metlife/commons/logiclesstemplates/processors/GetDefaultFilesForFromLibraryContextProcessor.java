package com.metlife.commons.logiclesstemplates.processors;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.commons.util.ContextProcessorUtils;
import com.metlife.commons.util.FormsLibraryConstants;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**.
 * GetDefaultFilesForFromLibraryContextProcessor
 *
 * This context processor must receive an array of references
 * to values in the content, each one of these references must
 * be a path to a file in the dam and the context processor
 * will add a list of files details to the content model.
 *
 * Each element of the resultant list will have the next structure
 * {
 *      file_size: "16 KB",
 *      file_description: "This is the life insurance 1",
 *      eform_size: "",
 *      eform_url: "",
 *      file_title: "Life insurance 1",
 *      file_url: "/content/dam/metlifecom/global/forms/life_insurance/life_insurance_1.pdf",
 *      file_type: "PDF",
 *      file_category_title: "Life Insurance"
 * }
 * and the list will be stored in content.defaultFiles
 *
 * Example:
 * xk_defaultForms: [content.file1, content.file2, content.file3, content.file4, content.file5]
 *
 * Where
 * fileN stores the path to a file in the dam
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
public class GetDefaultFilesForFromLibraryContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * DEFAULT_FORMS_PROPERTY_NAME.
     * This is the name of the property that must be configured
     * to execute this content processor.
     */
    public static final String DEFAULT_FORMS_PROPERTY_NAME =
            "xk_defaultForms";
    /**
     * ICONS_LIST_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String ICONS_LIST_CONFIGURATION_PROPERTY_NAME =
            "xk_iconsList";
    /**
     * DEFAULT_FILES.
     * Name of the content model property where the
     * resultant list will be stored.
     */
    private static final String DEFAULT_FILES = "content.defaultFiles";
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
            GetDefaultFilesForFromLibraryContextProcessor.class);

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(DEFAULT_FORMS_PROPERTY_NAME, ICONS_LIST_CONFIGURATION_PROPERTY_NAME);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        final Resource resource = slingHttpServletRequest.getResource();
        final XCQBConfiguration configuration = configurationProvider.getFor(
                slingHttpServletRequest.getResource().getResourceType());
        final String configuredIconListPath = configuration.asString(ICONS_LIST_CONFIGURATION_PROPERTY_NAME);
        final PageManager pageManager = slingHttpServletRequest.getResourceResolver().adaptTo(PageManager.class);
        final Page listPage = pageManager.getPage(configuredIconListPath);
        if (listPage != null) {
            final GenericList iconsGenericList = listPage.adaptTo(GenericList.class);
            final List<GenericList.Item> iconsList = iconsGenericList.getItems();
            final Collection<String> configuredFiles = ContextProcessorUtils.getPathRefListKeyNames(
                    configurationProvider, resource, DEFAULT_FORMS_PROPERTY_NAME);
            final List<Map<String, Object>> categoryFiles = new LinkedList();
            for (final String filePathConfig : configuredFiles) {
                final String filePath = contentModel.getAsString(filePathConfig);
                if (!filePath.equals(Constants.BLANK) && filePath != null) {
                    final Resource file = resourceResolver.getResource(filePath);
                    if (file != null && file.getResourceType().equals(FormsLibraryConstants.DAM_ASSET)) {
                        Resource parent = resourceResolver.getResource(filePath);
                        String categoryName = Constants.BLANK;
                        final Resource childJcrContent = file.getChild(Constants.JCR_CONTENT);
                        if (childJcrContent != null) {
                            final Resource fileMetadata = childJcrContent.getChild(
                                    FormsLibraryConstants.METADATA);
                            if (fileMetadata != null) {
                                final ValueMap fileMetadataMap = fileMetadata.adaptTo(ValueMap.class);
                                while (parent.getParent() != null) {
                                    if (parent.getResourceType().equals(FormsLibraryConstants.SLING_ORDERED_FOLDER)) {
                                        final Resource parentChildJcrContent = parent.getChild(
                                                Constants.JCR_CONTENT);
                                        if (parentChildJcrContent != null) {
                                            categoryName = (String) parentChildJcrContent.adaptTo(
                                                    ValueMap.class).get(FormsLibraryConstants.JCR_TITLE);
                                            break;
                                        } else {
                                            logger.error("parentChildJcrContent is null");
                                        }
                                    } else {
                                        parent = parent.getParent();
                                    }
                                }
                                final Map<String, Object> fileContainer = Maps.newHashMap();
                                fileContainer.put(FormsLibraryConstants.FILE_CATEGORY_TITLE, categoryName);
                                fileContainer.put(FormsLibraryConstants.FILE_URL, file.adaptTo(Asset.class).getPath());
                                fileContainer.put(FormsLibraryConstants.EFORM_URL, Constants.BLANK);
                                fileContainer.put(FormsLibraryConstants.FILE_TITLE, fileMetadataMap.get(
                                        MetLifeConstants.DC_TITLE));
                                fileContainer.put(FormsLibraryConstants.FILE_DESCRIPTION, fileMetadataMap.get(
                                        MetLifeConstants.DC_DESCRIPTION));
                                fileContainer.put(FormsLibraryConstants.FILE_TYPE,
                                        FilenameUtils.getExtension(file.getName()).toUpperCase());
                                final String iconName =
                                        GetTypeOfFileIconContextProcessor.findImageNameFromIconsList(
                                        iconsList, file.getName());
                                fileContainer.put(FormsLibraryConstants.FORM_ICON_PATH, iconName);
                                fileContainer.put(FormsLibraryConstants.FILE_SIZE, FileUtils.byteCountToDisplaySize(
                                        (Long) fileMetadataMap.get(MetLifeConstants.DAM_SIZE)));
                                fileContainer.put(FormsLibraryConstants.EFORM_SIZE, Constants.BLANK);
                                categoryFiles.add(fileContainer);
                            } else {
                                logger.error("fileMetadata is null");
                            }
                        } else {
                            logger.error("childJcrContent is null");
                        }
                    } else {
                        logger.error("file is null or not a dam:Asset resource");
                    }
                } else {
                    logger.error("The filePath is empty or null.");
                }
            }
            contentModel.set(DEFAULT_FILES, categoryFiles);
        }
    }
}
