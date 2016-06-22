package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xumak.base.assets.AssetPathService;
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
import java.util.Collections;
import java.util.Set;

import com.xumak.base.Constants;

/**
 * SpoolImagePathsContextProcessor
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/01/11 | Pablo Alecio    | Initial Creation
 * ----------------------------------------------------------------------------
 * the imagesConfigList property on the xk.config nodes needs to have the
 * following structure:
 * <context>::<relative-path-to-image-resource>::<name-of-the-content-model
 * -property>
 * e.g. global::megamenu/image::megaMenuImagePath
 * With the config above, the spooled image path should be under global
 * .megamenuImagePath
 */

@Component
@Service
public class SpoolImagePathsContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * IMAGES_CONFIGURATION_LIST_PROPERTY_NAME.
     */
    public static final String IMAGES_CONFIGURATION_LIST_PROPERTY_NAME =
            "imagesConfigList";
    /**
     * DOUBLE_COLON.
     */
    public static final String DOUBLE_COLON = "::";
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
     * Looks for the imageConfigList key name under config. If not found,
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
        final int three = 3;
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Resource resource = request.getResource();
        for (String imageConfig : getImageConfigList(resource)) {
            final String[] imageConfigArray = imageConfig.split(DOUBLE_COLON);
            if (imageConfigArray.length == three) { //image configs must
                                                    // have 3 parts
                final String context = imageConfigArray[0];
                final String resourcePath = imageConfigArray[1];
                final String contentModelPropertyName = imageConfigArray[2];
                Resource baseResource = resource;
                switch (context) {
                    case GLOBAL:
                        final String globalPath = contentModel.getAsString(
                                Constants.COMPONENT + Constants.DOT
                                        + GLOBAL_PATH_PN);
                        baseResource = resourceResolver.getResource(globalPath);
                        break;
                    case DESIGN:
                        //TODO implement
                        break;
                    default:
                        break;
                }

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

