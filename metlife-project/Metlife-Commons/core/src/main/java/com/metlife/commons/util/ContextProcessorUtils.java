package com.metlife.commons.util;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dtello on 2016/7/15.
 */
public final class ContextProcessorUtils {

    /**
     * VAR_DIR.
     * Constant String that references the /var directory
     */
    private static final String VAR_DIR = "/var";

    /**
     * DAM_DIR.
     * Constant String that references the /dam directory
     */
    private static final String DAM_DIR = "/dam";

    /**
     * ContextProcessorUtils.
     */
    private ContextProcessorUtils() {

    }

    /**
     * Looks for the pathRefs key name under config.
     *
     * @param configurationProvider the configuration provider
     * @param resource the resource
     * @param configurationPropertyName String with the configuration name
     * @return the name of the key where the traversed list base path is stored
     * @throws Exception throws exception
     */
    public static Collection<String> getPathRefListKeyNames(
            final XCQBConfigurationProvider configurationProvider,
            final Resource resource,
            final String configurationPropertyName)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final Collection<String> configurationPathRefListKeyName =
                configuration.asStrings(configurationPropertyName);
        return configurationPathRefListKeyName;
    }

    /**.
     * This method finds a file in the dam for the GetFileSizeContextProcessor and
     * GetTypeOfFileIconContextProcessor
     * @param slingHttpServletRequest the request
     * @param filePathParts the file path parts
     * @return an asset representing the file
     * @throws Exception if file is not found
     */
    public static Object getFileFromDam(final SlingHttpServletRequest slingHttpServletRequest,
            final String[] filePathParts) throws Exception {
        final AssetManager assetManager = slingHttpServletRequest.getResourceResolver().adaptTo(AssetManager.class);
        final ArrayList<Asset> files = new ArrayList<Asset>();
        Object file = null;
        final Resource childNode = slingHttpServletRequest.getResource().getChild(filePathParts[0]);
        if (childNode != null) {
            final Object filesPath = childNode.adaptTo(ValueMap.class).get(filePathParts[1]);
            if (filesPath instanceof String[]) {
                final String[] stringsfilePath = (String[]) filesPath;
                for (final String filePath: stringsfilePath) {
                    if (filePath.contains(DAM_DIR)) {
                        final String damPath = VAR_DIR + filePath.substring(filePath.indexOf(DAM_DIR));
                        files.add(assetManager.getAssetForBinary(damPath));
                    } else {
                        files.add(null);
                    }
                }
                file = files.toArray(new Asset[files.size()]);
            } else {
                if (filePathParts[1] != null) {
                    final String filePath = (String) childNode.adaptTo(ValueMap.class).get(filePathParts[1]);
                    if (filePath != null) {
                        if (filePath.contains(DAM_DIR)) {
                            final String damPath = VAR_DIR + filePath.substring(filePath.indexOf(DAM_DIR));
                            file = assetManager.getAssetForBinary(damPath);

                        } else {
                            throw new FileNotFoundException(filePath);
                        }
                    }
                }
            }
        }
        return file;
    }
}
