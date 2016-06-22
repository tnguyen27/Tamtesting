package com.metlife.poc.util;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Design;
import com.metlife.poc.logiclesstemplates.contextprocessors
        .UpdateMultifieldPropertyContextProcessor;
import com.xumak.base.Constants;
import com.xumak.base.assets.AssetPathService;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils
 * -----------------------------------------------------------------------------
 * CHANGE HISTORY
 * -----------------------------------------------------------------------------
 * Version | Date          | Developer              | Changes
 * 1.0.0   | 17/02/2014    | Gabriel Orozco         | Initial Creation
 * 1.1.0   | 06/05/2014    | Mario Rivas            | Added Global method to
 * map the properties inside.
 * 1.2.0   | 30/07/2015    | Mario Rivas            | Adding search of
 * property inside a multifield or
 * |               |                        | Multifield of Multifields.
 * 1.3.0   | 10/08/2015    | Eduardo León           | Adds String[]
 * deserialization support
 * 1.3.1   | 30/11/2015    | Jorge Escobar          | Adds remove .html
 * extension method
 * 1.3.2   | 09/12/2015    | Jorge Escobar          | Replace .html extension
 * whit "/" character
 * -----------------------------------------------------------------------------
 */
public final class Utils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);


    /**
     * Utils.
     */
    private Utils() {

    }

    /**
     * Get property as a List.
     *
     * @param contentModel Content Model
     * @param propName     Property Name
     * @return List of items
     */
    public static Collection getPropertyAsList(final ContentModel contentModel,
                                               final String propName) {
        Collection<Object> propValue = new ArrayList<Object>();
        if (contentModel.has(propName)) {
            if (contentModel.is(propName, Collection.class)) {
                try {
                    propValue = contentModel.getAs(propName, Collection.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (contentModel.get(propName).getClass().isArray()) {
                for (Object prop : (Object[]) contentModel.get(propName)) {
                    propValue.add(prop);
                }
            } else {
                propValue.add(contentModel.get(propName));

            }
        }
        return propValue;
    }

    /**
     * Returns the file extension of a given URI.
     *
     * @param uri URI
     * @return The file extension
     */
    public static String getExtension(final String uri) {

        final String extension;
        if (uri.indexOf(UpdateMultifieldPropertyContextProcessor.
                DOT_CHAR) > 0) {
            extension = uri.substring(uri.lastIndexOf(
                    UpdateMultifieldPropertyContextProcessor.DOT_CHAR));
        } else {
            extension = Constants.BLANK;

        }
        return extension;
    }

    /**
     * Get global node from Page Design.
     *
     * @param pageDesign       the page design
     * @param assetPathService instance of AssetPath Service
     * @return The node of the global properties
     */
    public static Node getGlobalFromDesign(final Design pageDesign, final
    AssetPathService assetPathService) {
        Node globalNode = null;
        if (pageDesign != null) {
            final Resource designResource = pageDesign.getContentResource();
            if (designResource != null) {
                final Node jcrContentNode = designResource.adaptTo(Node.class);
                try {
                    if (jcrContentNode.hasNode(Constants
                            .GLOBAL_PROPERTIES_KEY)) {
                        globalNode = jcrContentNode.getNode(Constants
                                .GLOBAL_PROPERTIES_KEY);
                    }
                } catch (RepositoryException re) {
                    LOGGER.error("Error in the repository, node could not be "
                            + "found: {}", re.getMessage(), re);
                } catch (Exception e) {
                    LOGGER.error("Error in the repository, node could not be "
                            + "found: {}", e.getMessage(), e);
                }
            }

        }
        return globalNode;
    }


    /**
     * Evaluate the property value.
     *
     * @param propName     Property to be evaluated
     * @param contentModel Content Model
     * @return returns TRUE if the property exists AND has Content
     * (if the content is boolean gets this value, otherwise returns TRUE).
     * @throws Exception Exception
     */
    public static boolean getValue(final String propName, final ContentModel
            contentModel) throws Exception {
        boolean value = Boolean.FALSE;
        boolean negate = Boolean.FALSE;
        String property = propName;
        if (propName.startsWith(MetLifeConstants.NOT)) {
            negate = Boolean.TRUE;
            property = propName.replace(MetLifeConstants.NOT, Constants.BLANK);
        }
        if (contentModel.has(property)) {
            final String propValue = contentModel.getAsString(property);
            if (StringUtils.isNotEmpty(propValue)) {
                if (contentModel.is(property, Boolean.class)) {
                    value = contentModel.getAs(property, Boolean.class);
                } else {
                    value = Boolean.TRUE;
                }
            } else {
                value = Boolean.FALSE;
            }
        } else {
            value = Boolean.FALSE;
        }
        if (negate) {
            value = !value;
        }
        return value;
    }

    /**
     * Externalizes the given resource link as an absolute URL based on the
     * request.
     *
     * @param request <code>SlingHttpServletRequest</code> object providing
     *                client request information.
     * @param link    <code>String</code> resource path to be externalized.
     * @return <code>String</code> an externalized link or an empty string
     * when the resource path is empty.
     */

    public static String getExternalizedLink(final SlingHttpServletRequest
                                                     request,
                                             final String link) {
        String getExternalizedLinkResult;
        if (!link.isEmpty()) {
            final Externalizer externalizer =
                    request.getResourceResolver().adaptTo(Externalizer.class);
            final Resource resource =
                    request.getResourceResolver().resolve(request, link);
            if (resource.getResourceType().equals(
                    Resource.RESOURCE_TYPE_NON_EXISTING)) {
                getExternalizedLinkResult = link;
            }
            getExternalizedLinkResult = externalizer.absoluteLink(request,
                MetLifeConstants.HTTP_PROTOCOL, link);
        } else {
            getExternalizedLinkResult = Constants.BLANK;
        }
        return getExternalizedLinkResult;
    }


    /**
     * Externalizes the urls inside an html block.
     *
     * @param input   html block
     * @param request html block with externalized links
     * @return return the externalized Link
     */
    public static String externalizeLinksInHTML(final String input, final
    SlingHttpServletRequest request) {
        String output = input;
        final Pattern pattern = Pattern.compile("href=\"(.*?)\"");
        final Matcher matcher = pattern.matcher(input);
        String url = null;
        while (matcher.find()) {
            url = matcher.group(1);
            output = input.replace(url, Utils.getExternalizedLink(request,
                    url));
        }
        return output;
    }


    /**
     * Searches for a property inside a Multifield or Multifield of
     * multifields structure.
     * It requires that the structure is deserealized to do the search.
     *
     * @param object   Strcuture that the multifield uses.
     * @param property Property to search inside the structure.
     * @return Result of the object of the property found.
     */
    public static Object getPropertyFromMultifield(final Object object, final
    String property) {
        Object resultObject = null;
        if (object instanceof Map) {
            if (((Map) object).containsKey(property)) {
                resultObject = ((Map) object).get(property);
            } else {
                final Map recursiveMap = (Map) object;
                final Set<String> setOfKeys = recursiveMap.keySet();
                final List<Object> resultList = new ArrayList<Object>();
                Object temporalObject = null;
                for (String key : setOfKeys) {
                    temporalObject = getPropertyFromMultifield(recursiveMap
                            .get(key), property);
                    if (temporalObject != null) {
                        if (temporalObject instanceof ArrayList) {
                            resultList.addAll((ArrayList) temporalObject);
                        } else {
                            resultList.add(temporalObject);
                        }
                    }
                }
                resultObject = resultList;
            }
        } else if (object instanceof ArrayList) {
            final List itemList = (ArrayList) object;
            final List<Object> resultList = new ArrayList<Object>();
            Object resultItem = null;
            for (Object item : itemList) {
                resultItem = getPropertyFromMultifield(item, property);
                if (resultItem != null) {
                    if (resultItem instanceof ArrayList) {
                        resultList.addAll((ArrayList) resultItem);
                    } else {
                        resultList.add(resultItem);
                    }
                }
            }
            resultObject = resultList;

        }
        return resultObject;
    }

    /**
     * Gets Latamlink path and resolves to remove .html extension from local
     * urls.
     *
     * @param path    Resource path
     * @param request Current sling request
     * @return url without .html extension
     */

    public static String removeHTMLExtension(final String path,
        final SlingHttpServletRequest request) {
        String result = "";
        final WCMMode mode = WCMMode.fromRequest(request);
        if (mode != WCMMode.EDIT) {
            if (path.contains(MetLifeConstants.SLASH_CONTENT)) {
                final String newPath =
                        path.replace(MetLifeConstants.HTML_EXTENSION,
                    Constants.SLASH);
                result = newPath;

            }
        } else {
            result = path;
        }
        return result;

    }
}
