package com.metlife.poc.logiclesstemplates.contextprocessors;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.Lists;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.assets.AssetPathService;
import com.xumak.base.templatingsupport.TemplateContentModel;
import com.xumak.base.util.ResourceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AddSpooledImagePathContextProcessor
 *
 * This Context Processor spools images in global, design and content contexts.
 * In order to spool an image resource the following properties should be used
 * in xk.config node:
 * - xk.spoolContentImages: content context resources
 * - xk.spoolGlobalImages: global context resources
 * - xk.spoolDesignImages: design context resources
 *
 * For example, if we want to spool image resources on global context the
 * configuration
 * should be the following:
 *
 * xk.spoolGlobalImages: ['image1resourcename', 'image2resourcename']
 *
 * **Note that only the resource names should be specified, the absoulte path
 * is then
 * added by the Context Processor.
 * -----------------------------------------------------------------------------
 * CHANGE HISTORY
 * -----------------------------------------------------------------------------
 * Version | Date          | Developer              | Changes
 * 1.0     | 29/01/2015    | Carlos Barrios         | Initial Creation
 * 2.0     | 13/02/2015    | Gabriel Orozco         | Added support for spool
 * |               |                        | multiple images on global,
 * |               |                        | design and content contexts
 * 2.1     | 20/10/2015    | Lesly Quinonez         | Resolve images' urls
 * -----------------------------------------------------------------------------
 */

@Component
@Service
public class AddSpooledImagePathContextProcessor
        extends AbstractConfigurationContextProcessor<TemplateContentModel> {

    /**
     * assetPathService.
     */
    @Reference
    private AssetPathService assetPathService;

    /**
     * resourceResolverFactory.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 1;
    } //must be executed after AddDeserializedJsonListContextProcessor


    @Override
    protected final boolean mustExist() {
        return false;
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList(MetLifeConstants.GLOBAL_IMAGE_RESOURCES_KEY,
                MetLifeConstants.DESIGN_IMAGE_RESOURCES_KEY,
                MetLifeConstants.CONTENT_IMAGE_RESOURCES_KEY,
                MetLifeConstants.PAGE_IMAGE_RESOURCES_KEY);
    }

    /**
     * This method is used to get all the configuration properties required
     * on the context processor.
     *
     * @return List of required configuration properties
     */
    @Override
    public final Set<String> requiredPropertyNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {

        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Resource resource = request.getResource();
        //getting image resource names from xk.config
        final List<String> globalImageResources =
                (List<String>) Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + MetLifeConstants.GLOBAL_IMAGE_RESOURCES_KEY);
        final List<String> designImageResources =
                (List<String>) Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + MetLifeConstants.DESIGN_IMAGE_RESOURCES_KEY);
        final List<String> contentImageResources =
                (List<String>) Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + MetLifeConstants.CONTENT_IMAGE_RESOURCES_KEY);
        final List<String> pageImageResources =
                (List<String>) Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + MetLifeConstants.PAGE_IMAGE_RESOURCES_KEY);
        //spool images located in this resource
        spoolImageResources(resourceResolver, resource, contentModel,
                globalImageResources, MetLifeConstants.GLOBAL_CONTEXT);
        spoolImageResources(resourceResolver, resource, contentModel,
                designImageResources, MetLifeConstants.DESIGN_CONTEXT);
        spoolImageResources(resourceResolver, resource, contentModel,
                contentImageResources, MetLifeConstants.CONTENT_CONTEXT);
        spoolImageResources(resourceResolver, resource, contentModel,
                pageImageResources, MetLifeConstants.PAGE_CONTEXT);


    }

    /**
     * spoolImageResources.
     * @param resourceResolver The resource resolver.
     * @param resource The resource.
     * @param contentModel The content model.
     * @param imageResources List of image resources.
     * @param context int context
     */
    private void spoolImageResources(final ResourceResolver resourceResolver,
                                     final Resource resource,
                                     final TemplateContentModel contentModel,
                                     final List<String> imageResources,
                                     final int context) {
        if (CollectionUtils.isNotEmpty(imageResources)) {
            for (String imageResourcePath : imageResources) {
                spoolImageResource(resourceResolver, resource, contentModel,
                        imageResourcePath, context);
            }
        }
    }


    /**
     * spoolImageResource.
     * @param resourceResolver The resource resolver.
     * @param resource The resource.
     * @param contentModel The content model.
     * @param imageResourcePath The image resource path.
     * @param context int context.
     */
    private void spoolImageResource(final ResourceResolver resourceResolver,
                                    final Resource resource,
                                    final TemplateContentModel contentModel,
                                    final String imageResourcePath,
                                    final int context) {

        if (StringUtils.isNotBlank(imageResourcePath)) {
            final String imageResourceAbsolutePath =
                    getAbsoluteImageResourcePath(resourceResolver, resource,
                            contentModel, imageResourcePath, context);
            final Resource imageResource = resourceResolver.getResource(
                    imageResourceAbsolutePath);
            final Map<String, Object> configObject = (Map<String, Object>)
                    contentModel.get(Constants.CONFIG_PROPERTIES_KEY);
            if (null != imageResource) {
                if (hasProperty(imageResource, Constants.FILE_REFERENCE)) {
                    try {
                        //spooling image's path
                        final String spooledPath = assetPathService
                                .getComponentImagePath(imageResource);
                        final Map<String, String> renditions =
                                getImageRenditions(imageResource, configObject);
                        //putting spooled path in context
                        final String spooledPathPropertyName =
                                getImageName(imageResourcePath) + StringUtils
                                        .capitalize(MetLifeConstants.SPOOLED);
                        final Map<String, Object> contextMap = getContextMap(
                                contentModel, context);
                        contextMap.put(spooledPathPropertyName,
                                resourceResolver.map(spooledPath));
                        if (renditions != null) {
                            contextMap.put(spooledPathPropertyName
                                    + MetLifeConstants.UNDERSCORE
                                    + MetLifeConstants.RENDITIONS, renditions);
                        }
                    } catch (Exception e) {
                        log.error("There was a problem trying to get the "
                                + "spooled image.", e);
                    }
                } else if (hasProperty(imageResource,
                        Constants.FILE_REFERENCES)) {

                    //getting paths of images
                    final List<String> fileReferences =
                            ResourceUtils.getPropertyAsStrings(imageResource,
                                    Constants.FILE_REFERENCES);
                    //for each path, spool it and then add them to context
                    if (null != fileReferences) {
                        final List<Object> spooledPaths =
                                new ArrayList<Object>();
                        for (int i = 0; i < fileReferences.size(); i++) {
                            try {
                                final String spooledPath = assetPathService
                                        .getComponentAssetPath(imageResource,
                                                i);

                                if (!spooledPath.isEmpty()) {
                                    if (hasRenditions(configObject,
                                            imageResource.getName())) {
                                        final Map<String, Object> asset = new
                                                HashMap<>();
                                        asset.put(MetLifeConstants.ORIGINAL,
                                                resourceResolver
                                                .map(spooledPath));
                                        final Map<String, String> renditions =
                                                getImageRenditions(
                                                        imageResource,
                                                                configObject,
                                                                i);
                                        asset.put(MetLifeConstants.RENDITIONS,
                                                renditions);
                                        spooledPaths.add(asset);
                                    } else {
                                        spooledPaths.add(resourceResolver.map(
                                                spooledPath));
                                    }
                                }
                            } catch (Exception e) {
                                log.error("There was a problem trying to get "
                                        + "the spooled image.", e);
                            }
                        }

                        //putting spooled paths in context
                        final String spooledPathsPropertyName =
                                getImageName(imageResourcePath) + StringUtils
                                        .capitalize(MetLifeConstants.SPOOLED);
                        final Map<String, Object> contextMap = getContextMap(
                                contentModel, context);
                        contextMap.put(spooledPathsPropertyName, spooledPaths);
                    }
                }
            }
        }
    }

    /**
     * Get absolute path for resource.
     * @param resourceResolver The resource resolver.
     * @param resource The resource.
     * @param contentModel The content model.
     * @param imageResourcePath The image resource path.
     * @param context int context.
     * @return Path of the resource as a string.
     */
    private String getAbsoluteImageResourcePath(final ResourceResolver
                                                        resourceResolver,
                                                final Resource resource,
                                                final TemplateContentModel
                                                        contentModel,
                                                final String imageResourcePath,
                                                final int context) {
        final String basePath;
        switch (context) {
            case MetLifeConstants.GLOBAL_CONTEXT:
                basePath = getGlobalPath(contentModel);
                break;
            case MetLifeConstants.DESIGN_CONTEXT:
                final Designer designer = resourceResolver.adaptTo(Designer
                        .class);
                final Style style = designer.getStyle(resource);
                basePath = style.getPath();
                break;
            case MetLifeConstants.PAGE_CONTEXT:
                final PageManager pageManager = resourceResolver.adaptTo(
                        PageManager.class);
                final Page currentPage = pageManager.getContainingPage(
                        resource);
                basePath = currentPage.getContentResource().getPath();
                break;
            default:
                basePath = getContentPath(contentModel);
                break;
        }
        return basePath + Constants.SLASH + imageResourcePath;
    }

    /**
     * Get content path.
     * @param contentModel TemplateContentModel.
     * @return Content path as string.
     */
    private String getContentPath(final TemplateContentModel contentModel) {
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        return MapUtils.getString(contentObject, Constants.PATH,
                Constants.CONTENT_ROOT);
    }

    /**
     * Get design path.
     * @param contentModel TemplateContentModel.
     * @return Design path as string.
     */
    private String getDesignPath(final TemplateContentModel contentModel) {
        final Map<String, Object> pageObject = (Map<String, Object>)
                contentModel
                .get(Constants.GLOBAL_PAGE_CONTENT_KEY);
        return MapUtils.getString(pageObject,
                MetLifeConstants.CQ_DESIGN_PATH_KEY,
                MetLifeConstants.DEFAULT_DESIGN_PATH);
    }

    /**
     * Get global path.
     * @param contentModel TemplateContentModel.
     * @return Global path as String.
     */
    private String getGlobalPath(final TemplateContentModel contentModel) {
        final Map<String, Object> componentObject = (Map<String, Object>)
                contentModel.get(Constants.COMPONENT_PROPERTIES_KEY);
        String globalPath = MapUtils.getString(componentObject,
                Constants.GLOBAL_PATH_PROPERTY_KEY);
        if (StringUtils.isBlank(globalPath)) {
            globalPath = getDesignPath(contentModel);
            globalPath = globalPath + Constants.SLASH + Constants.JCR_CONTENT
                    + Constants.SLASH
                    + Constants.GLOBAL_PROPERTIES_KEY;
        }
        return globalPath;
    }

    /**
     * Check if resource has property.
     * @param resource Resource to search for the property.
     * @param propertyName Property name.
     * @return True if the property is found, else false is returned.
     */
    private boolean hasProperty(final Resource resource, final String
            propertyName) {
        boolean hasProperty = false;
        try {
            final Node node = resource.adaptTo(Node.class);
            if (node.hasProperty(propertyName)) {
                hasProperty = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasProperty;
    }

    /**
     * Get image name.
     * @param imageResourcePath Image path as string.
     * @return Image name as string.
     */
    private String getImageName(final String imageResourcePath) {
        final String imageName;
        if (imageResourcePath.contains(Constants.SLASH)) {
            final int lastSlash = imageResourcePath.lastIndexOf(
                    MetLifeConstants.SLASH_CHAR);
            imageName = imageResourcePath.substring(lastSlash);
        } else {
            imageName = imageResourcePath;
        }
        return imageName;
    }

    /**
     * Get context map.
     * @param contentModel TemplateContentModel.
     * @param context context int
     * @return Map
     */
    private Map<String, Object> getContextMap(final TemplateContentModel
                                                      contentModel,
                                              final int context) {
        final Map<String, Object> map;
        switch (context) {
            case MetLifeConstants.GLOBAL_CONTEXT:
                map = (Map<String, Object>) contentModel.get(
                        Constants.GLOBAL_PROPERTIES_KEY);
                break;
            case MetLifeConstants.DESIGN_CONTEXT:
                map = (Map<String, Object>) contentModel.get(
                        Constants.DESIGN_PROPERTIES_KEY);
                break;
            case MetLifeConstants.PAGE_CONTEXT:
                map = (Map<String, Object>) contentModel.get(
                        Constants.GLOBAL_PAGE_CONTENT_KEY);
                break;
            default:
                map = (Map<String, Object>) contentModel.get(
                        Constants.RESOURCE_CONTENT_KEY);
                break;
        }
        return map;
    }

    /**
     * Get image renditions.
     * @param resource The resource.
     * @param configObject The configuration map.
     * @return Map.
     * @throws Exception throws exception.
     */
    private Map<String, String> getImageRenditions(final Resource resource,
                                                   final Map<String, Object>
                                                           configObject)
            throws Exception {
        return getImageRenditions(resource, configObject, -1);
    }

    /**
     * Get image renditions.
     * @param resource The resource.
     * @param configObject The configuration map.
     * @param index The index.
     * @return Map.
     * @throws Exception throws exception.
     */
    private Map<String, String> getImageRenditions(final Resource resource,
                                                   final Map<String, Object>
                                                           configObject,
                                                   final int index) throws
            Exception {
        Map<String, String> imageRenditions = null;
        if (hasRenditions(configObject, resource.getName())) {
            imageRenditions = new HashMap<>();
            final Object renditions =
                    configObject.get(resource.getName() + Constants.DOT
                            + MetLifeConstants.RENDITIONS);

            if (renditions instanceof String) {
                final String rendition = (String) renditions;
                final String assetSpoolPath =
                        assetPathService.getComponentAssetPath(resource,
                                rendition, index);
                imageRenditions.put(rendition, resource.getResourceResolver()
                        .map(assetSpoolPath));
            } else {
                final Collection<String> renditionsArray =
                        (Collection) renditions;
                for (String rendition : renditionsArray) {
                    final String assetSpoolPath =
                            assetPathService.getComponentAssetPath(resource,
                                    rendition, index);
                    imageRenditions.put(rendition, resource
                            .getResourceResolver()
                            .map(assetSpoolPath));
                }
            }
        }
        return imageRenditions;
    }

    /**
     * Has renditions.
     * @param configObject Configuration object.
     * @param resourceName The resource name.
     * @return True if resourceName + DOT + RENDITIONS is found on the configu
     * ration object.
     */
    private boolean hasRenditions(final Map configObject, final String
            resourceName) {
        return configObject.containsKey(resourceName + Constants.DOT
                + MetLifeConstants.RENDITIONS);
    }
}
