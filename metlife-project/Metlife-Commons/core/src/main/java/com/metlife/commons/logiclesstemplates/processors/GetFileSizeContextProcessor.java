package com.metlife.commons.logiclesstemplates.processors;

import com.day.cq.dam.api.Asset;
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
import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**.
 * GetFileSizeContextProcessor
 * This context processor will get a file from the configured property
 * and then will add the file size to the content model.
 *
 * Example:
 * xk_getFileSize: pdflink::fileReferences
 *
 * Where
 * pdflink::fileReferences is the path to a file in the dam
 *
 * Then
 * the context processor will add the content.pdflinkSize to the
 * content model in the format "xyz (KB | MB)"
 * For multifields the context processor will add the fileSize.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/07/15 | Diego Tello     | Initial creation
 * 2.0     | 2016/07/25 | Sergio Torres   | Addapt to accept multifields.
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class GetFileSizeContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * CONFIGURATION_PROPERTY_NAME.
     */
    public static final String CONFIGURATION_PROPERTY_NAME =
            "xk_getFileSize";

    /**
     * MULTIFILES_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String MULTIFILES_CONFIGURATION_PROPERTY_NAME =
            "xk_getMultiFileSize";

    /**
     * ILLEGAL_FORMAT_EXCEPTION.
     * This is the message that will be shown in the exception if the configured value does not
     * comply with the required format, ex: "content.categories::categoryName"
     */
    private static final String ILLEGAL_FORMAT_EXCEPTION =
            "The " + CONFIGURATION_PROPERTY_NAME + " value does not have the required format.";

    /**
     * DAM_SIZE.
     * Constant String that references the dam:size property
     */
    private static final String DAM_SIZE = "dam:size";

    /**
     * SIZE.
     * Constant String to concatenate to the content property
     */
    private static final String SIZE = "Size";

    /**
     * FILE SIZE.
     * Constant String to concatenate to the content property
     */
    private static final String FILE_SIZE = "fileSize";

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Logger.
     */
    //private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
    //        GetFileSizeContextProcessor.class);

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY;
    }

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(CONFIGURATION_PROPERTY_NAME);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final Collection<String> configuredFilePathLocations = ContextProcessorUtils.getPathRefListKeyNames(
                configurationProvider, slingHttpServletRequest.getResource(), CONFIGURATION_PROPERTY_NAME);
        for (final String filePathLocation: configuredFilePathLocations) {
            final String[] filePathParts = filePathLocation.split(MetLifeConstants.DOUBLE_COLON);
            if (filePathParts.length == 2) {
                final Object file = ContextProcessorUtils.getFileFromDam(slingHttpServletRequest, filePathParts);
                if (file != null) {
                    if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + MULTIFILES_CONFIGURATION_PROPERTY_NAME)) {
                        final XCQBConfiguration configuration = configurationProvider.getFor(
                                slingHttpServletRequest.getResource().getResourceType());
                        final String listName = configuration.asString(MULTIFILES_CONFIGURATION_PROPERTY_NAME);
                        final Collection<Object> listFiles = Utils.getPropertyAsList(contentModel,
                                Constants.CONTENT + Constants.DOT + listName);
                        Asset[] arrayFiles = new Asset[configuredFilePathLocations.size()];
                        if (file instanceof Asset) {
                            final Asset assetsFiles = (Asset) file;
                            arrayFiles[0] = assetsFiles;
                        } else if (file instanceof Asset[]) {
                            arrayFiles = (Asset[]) file;
                        }
                        int count = 0;
                        final Iterator<Object> keys = listFiles.iterator();
                        while (keys.hasNext()) {
                            final Object key = keys.next();
                            final Map<String, Object> currentItem = (Map<String, Object>) key;
                            if (count < arrayFiles.length && arrayFiles[count] != null) {
                                final Map<String, Object> metadataFile = arrayFiles[count].getMetadata();
                                if (metadataFile != null) {
                                    final Long fileSize = (Long) metadataFile.get(DAM_SIZE);
                                    final String size = FileUtils.byteCountToDisplaySize(fileSize);
                                    currentItem.put(FILE_SIZE, size);
                                }
                                count++;
                            }
                        }
                    } else {
                        if (file instanceof Asset) {
                            final Asset assetFile = (Asset) file;
                            final Long fileSize = (Long) assetFile.getMetadata().get(DAM_SIZE);
                            contentModel.set(Constants.CONTENT + Constants.DOT + filePathParts[0] + SIZE,
                                    FileUtils.byteCountToDisplaySize(fileSize));
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException(ILLEGAL_FORMAT_EXCEPTION);
            }
        }
    }
}
