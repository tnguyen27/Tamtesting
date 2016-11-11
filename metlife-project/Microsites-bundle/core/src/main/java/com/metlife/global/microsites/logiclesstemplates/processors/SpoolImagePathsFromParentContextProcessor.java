package com.metlife.global.microsites.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.global.microsites.util.ContextProcessorUtils;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.assets.AssetPathService;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * SpoolImagePathsContextProcessor
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/07/07 | Diego Tello     | Initial Creation
 *                                        | Refactoring from original
 *                                        | SpoolImagePathsContextProcessor
 * ----------------------------------------------------------------------------
 * the imagesFromParentConfigList property on the xk.config nodes needs to have the
 * following structure:
 * <parent>::</><context>::<relative-path-to-image-resource>::<name-of-the-content-model
 * -property>
 * e.g. metlifeMicrosites/components/page/homepage::global::megamenu/image::megaMenuImagePath
 * With the config above, the spooled image path should be under global
 * .megamenuImagePath
 * The images will be spooled from the parent or the same page if the parent is the same
 * as the current resource
 */
@Component
@Service
public class SpoolImagePathsFromParentContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * IMAGES_CONFIGURATION_LIST_PROPERTY_NAME.
     */
    public static final String IMAGES_CONFIGURATION_LIST_PROPERTY_NAME =
            "xk_imagesFromParentConfigList";

    /**
     * GLOBAL.
     */
    public static final String GLOBAL = "global";
    /**
     * DESIGN.
     */
    public static final String DESIGN = "design";
    /**
     * GLOBAL_PATH_PN.
     */
    public static final String GLOBAL_PATH_PN = "globalPath";
    /**
     * GLOBAL_PATH_PN.
     */
    public static final int FOUR = 4;

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * assetPathService.
     */
    @Reference
    private AssetPathService assetPathService;

    @Override
    public final boolean mustExist() {
        return false;
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
     * Get required property names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(IMAGES_CONFIGURATION_LIST_PROPERTY_NAME);
    }

    /**
     * Looks for the imageFromParentConfigList key name under config. If not found,
     * returns an empty list
     *
     * @param resource the request resource
     * @return the name of the key where the traversed list base path is stored
     * @throws Exception throws exception
     */
    @SuppressWarnings("unchecked")
    private Collection<String> getImageConfigList(final Resource resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        Collection<String> imageConfigList =
                configuration.asStrings(
                IMAGES_CONFIGURATION_LIST_PROPERTY_NAME);
        /*
        return imageConfigList.isEmpty()
                ?
                Collections.EMPTY_LIST
                :
                imageConfigList;
                */
        if (imageConfigList.isEmpty()) {
            imageConfigList = Collections.EMPTY_LIST;
        }
        return imageConfigList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
            ContentModel contentModel) throws Exception {
        final Resource resource = request.getResource();
        for (final String imageConfig : getImageConfigList(resource)) {
            final String[] imageConfigArray = imageConfig.split(MetLifeConstants.DOUBLE_COLON);
            if (imageConfigArray.length == FOUR) { //image configs must
                // have 4 parts
                final String resourceType = imageConfigArray[0];
                final String context = imageConfigArray[1];
                final String resourcePath = imageConfigArray[2];
                final String contentModelPropertyName = imageConfigArray[MetLifeConstants.THREE];
                final Resource baseResource = ContextProcessorUtils.findReferencedParentPage(request, resourceType);
                if (baseResource != null) {
                    final Resource imageResource = baseResource.getChild(
                            resourcePath);
                    if (imageResource != null) {
                        final String imagePath = assetPathService
                                .getComponentImagePath(imageResource);
                        contentModel.set(context + Constants.DOT
                                + contentModelPropertyName, imagePath);
                    }
                }
            }
        }

    }
}
