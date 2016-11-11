package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Sets;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.assets.AssetPathService;
import com.xumak.base.templatingsupport.ContentModel;
import com.xumak.base.util.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * AddResourcePropertiesContextProcessor
 * Context processor that obtains the properties of a specific resource.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 05/24/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component
@Service
public class AddResourcePropertiesContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    private static final String SPOOLED_IMAGE = "spooledImage";
    private static String xkResourcePath = "xk_resourcePath";
    private static String suffix = "ResourceProperties";
    private static String separatorPattern = ">>";

    @Reference
    private AssetPathService assetPathService;

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(xkResourcePath);
    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Sets.newHashSet();
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel) throws Exception {
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final List<String> resourcePathPropertyList =
                (List<String>) Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                                + xkResourcePath);

        if (CollectionUtils.isNotEmpty(resourcePathPropertyList)) {
            for (String resourcePathProperty : resourcePathPropertyList) {
                if (StringUtils.isNotEmpty(resourcePathProperty)) {
                    final Resource resource = getResource(resourceResolver, contentModel, resourcePathProperty);
                    if (resource != null) {
                        final Node node = resource.adaptTo(Node.class);
                        final Map<String, Object> properties = PropertyUtils.propsToMap(resource);
                        final TreeMap<String,  Object> orderPropeties = new TreeMap<>(properties);

                        iterateComponents(node, orderPropeties, resourceResolver);
                        final String nodeProperty = getResourcePath(resourcePathProperty)
                                + suffix
                                + getResourceExtraSuffix(resourcePathProperty);
                        contentModel.set(nodeProperty, orderPropeties);
                    }
                }
            }
        }
    }

    /**
     *
     * @param node resource where is located all the properties.
     * @param properties map that has all the properties recollected before.
     * @param resourceResolver resource Resolver.
     * @throws Exception Throws when the node doesn't exist or repository issues.
     */
    private void iterateComponents(final Node node, final Map<String, Object> properties,
                                   ResourceResolver resourceResolver) throws Exception {
        if (node.hasNodes()) {
            final NodeIterator nodeIterator = node.getNodes();
            while (nodeIterator.hasNext()) {
                final Node child = nodeIterator.nextNode();

                final Map<String, Object> mapProps = PropertyUtils.propsToMap(child);
                final TreeMap<String, Object> oMapProps = new TreeMap<>(mapProps);

                if (isImageResource(child)) {
                    oMapProps.put(SPOOLED_IMAGE, getSpoolIamgeResource(child, resourceResolver));
            }
                properties.put(child.getName(), oMapProps);
                iterateComponents(child, (Map<String, Object>) properties.get(child.getName()), resourceResolver);
        }
    }
    }

    /**
     * Check if child is a image Resource.
     * @param chilNode node to check
     * @return true if is a image Resource
     */
    private boolean isImageResource(final Node chilNode) {
        boolean isImage = false;
        try {
            if (chilNode.hasProperty(MetLifeConstants.SLING_RESOURCE_TYPE)) {
                final Property slingRes = chilNode.getProperty(MetLifeConstants.SLING_RESOURCE_TYPE);
                if (slingRes.getValue().toString().equals(MetLifeConstants.IMAGE_RESOURCE_TYPE)) {
                    isImage = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return isImage;

    }
    /**
     * returns the spooled path from a external Resource.
     * @param chilNode node to check
     * @param resourceResolver request request
     * @return true if is a image Resource
     */
    private String getSpoolIamgeResource(final Node chilNode, final ResourceResolver resourceResolver) {
        final Resource imageResource;
        String spoolPath = "";
        try {
            imageResource = resourceResolver.getResource(chilNode.getPath());
            spoolPath = assetPathService
            .getComponentImagePath(imageResource);
            spoolPath = resourceResolver.map(spoolPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return spoolPath;

    }
    /**
     *
     * @param resourceResolver Resource resolver object.
     * @param contentModel Content Model object.
     * @param resourcePath resource information, it can have a structure **resource-path**>>**relative-node-path**
     *                     or only the resource path.
     * @return resource indicated in the resourcePath.
     */
    private Resource getResource(final ResourceResolver resourceResolver, final ContentModel contentModel,
                                 final String resourcePath) {
        Resource resource = null;
        if (StringUtils.isNotEmpty(resourcePath)) {
            if (resourcePath.contains(separatorPattern)
                    && resourcePath.split(separatorPattern).length == 2) {
                final String[] resourceInformation = resourcePath.split(separatorPattern);
                final String path = contentModel.getAsString(resourceInformation[0]);
                if (StringUtils.isNotEmpty(path)) {
                    resource = resourceResolver.getResource(path + Constants.SLASH + resourceInformation[1]);
                }
            } else {
                final String path = contentModel.getAsString(resourcePath);
                resource = resourceResolver.getResource(path);
            }
        }
        return resource;
    }

    /**
     *
     * @param resourcePath Resource information.
     * @return resource's path.
     */
    private String getResourcePath(final String resourcePath) {
        String path = resourcePath;
        if (resourcePath.contains(separatorPattern)) {
            path = resourcePath.split(separatorPattern)[0];
        }
        return path;
    }

    /**
     *
     *
     * @param resourcePath Resource information.
     * @return resource extra suffix
     * @throws Exception Throws when the resource doesn't exist or repository issues.
     */
    private String getResourceExtraSuffix(final String resourcePath) throws Exception {
        final String path;
        String extraSuffix = "";
        String[] extraPath;
        if (resourcePath.contains(separatorPattern)) {
            path = resourcePath.split(separatorPattern)[1];
            extraPath = path.split(Constants.SLASH);
            extraSuffix = extraPath[extraPath.length - 1];

        }
        return extraSuffix;
    }
}
