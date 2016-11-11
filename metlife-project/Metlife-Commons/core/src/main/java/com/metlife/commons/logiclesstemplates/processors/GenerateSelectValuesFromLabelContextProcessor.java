package com.metlife.commons.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.metlife.commons.util.MLCommonsConstants;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.JSONUtils;
import com.metlife.poc.util.MetLifeConstants;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**.
 * GenerateSelectValuesFromLabelContextProcessor
 *
 * Generate values for a selection field from a list of names using
 * the xk_generateValueFromLabel property.
 *
 * Example:
 * xk_generateValueFromLabel: content.categories::categoryName
 *
 * Where
 * content.categories is a reference to a list
 * And
 * categoryName is the key of the value inside the list element
 * to be used to generate the selection values
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/06/27 | Diego Tello     | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class GenerateSelectValuesFromLabelContextProcessor extends AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * Logger.
     */
    //private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
    //        GenerateSelectValuesFromLabelContextProcessor.class);

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * PATH_REFS_CONFIGURATION_PROPERTY_NAMES.
     */
    public static final String PATH_REFS_CONFIGURATION_PROPERTY_NAMES =
            "xk_generateValueFromLabel";

    /**
     * VALUE_PROPERTY_NAME_SUFFIX.
     * This is the suffix to identify the generated value, ex:
     * if the property value points to content.categories::categoryName the
     * generated value will be stored in content.categories::categoryName_value
     */
    private static final String VALUE_PROPERTY_NAME_SUFFIX =
            "_value";

    /**
     * ILLEGAL_FORMAT_EXCEPTION.
     * This is the message that will be shown in the exception if the configured value does not
     * comply with the required format, ex: "content.categories::categoryName"
     */
    private static final String ILLEGAL_FORMAT_EXCEPTION =
            "The " + PATH_REFS_CONFIGURATION_PROPERTY_NAMES + " value does not have the required format.";

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(PATH_REFS_CONFIGURATION_PROPERTY_NAMES);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel) throws Exception {
        final Resource resource = request.getResource();
        //This will be used to transform from map to json string
        final Gson gsonObj = new Gson();
        //This loops through the configured values for the xk_generateValueFromLabel property
        for (final String pathRefListKeyName : getPathRefListKeyNames(resource)) {
            //Check if the configured value complies with the required format
            if (pathRefListKeyName.contains(MetLifeConstants.DOUBLE_COLON)) {
                //Split the configured value by "::"
                //references[0] contains the first level reference e.g. content.categories
                //references[1] contains the key to look for in the list e.g. categoryName
                final String[] references = pathRefListKeyName.split(MetLifeConstants.DOUBLE_COLON);
                //Check if the result of the split contains only the two required parts, if not
                //it does not comply with the required format
                if (references.length != 2) {
                    throw new IllegalArgumentException(ILLEGAL_FORMAT_EXCEPTION);
                }
                //Get the content e.g. content.categories
                final Object objectList = contentModel.get(references[0]);
                //jsonObjectList contains the content list as a list of maps
                final List<Map> jsonObjectList;
                //jsonObjectListResult contains the content including the
                //generated values
                final List<String> jsonObjectListResult = new ArrayList<String>();
                //Transform the content to a list of maps
                if (objectList instanceof String[]) {
                    final String[] stList = (String[]) objectList;
                    final ArrayList<String> newList = new ArrayList(Arrays.asList(stList));
                    jsonObjectList = JSONUtils.jsonStringObjectToMapList(newList);
                } else {
                    if (objectList instanceof ArrayList && !((ArrayList) objectList).isEmpty()
                            && ((ArrayList) objectList).get(0) instanceof Map) {
                        jsonObjectList = (List<Map>) objectList;
                    } else {
                        jsonObjectList = JSONUtils.jsonStringObjectToMapList(objectList);
                    }
                }
                //Loop through the map list
                //Generate the values
                //Add the values to the result list
                for (final Map<String, String> jsonObject: jsonObjectList) {
                    final String name = jsonObject.get(references[1]);
                    if (jsonObject.size() > 0) {

                        final String generatedValue = generatedValueFromString(name);
                        jsonObject.put(references[1] + VALUE_PROPERTY_NAME_SUFFIX, generatedValue);
                        jsonObjectListResult.add(gsonObj.toJson(jsonObject));
                    }
                }
                //Set the content
                contentModel.set(references[0], jsonObjectListResult);
            } else {
                //Get content value
                final String contentValue = (String) contentModel.get(pathRefListKeyName);
                // If not null, replace the contentValue whitespaces for underscore and set it to lowercase
                if (contentValue != null) {
                    final String generatedValue = generatedValueFromString(contentValue);
                    //Set the content
                    contentModel.set(pathRefListKeyName + VALUE_PROPERTY_NAME_SUFFIX, generatedValue);
                }
            }
        }
    }

    /**
     * Looks for the pathRefs key name under config. If not found, gets the
     * default one
     *
     * @param resource the request resource
     * @return the name of the key where the traversed list base path is stored
     * @throws Exception throws exception
     */
    private Collection<String> getPathRefListKeyNames(final Resource resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        final Collection<String> configurationPathRefListKeyName =
                configuration.asStrings(PATH_REFS_CONFIGURATION_PROPERTY_NAMES);
        return configurationPathRefListKeyName;
    }

    /**
     * remove leading and trailing whiteSpaces and replaces middle spaces with underscores.
     * @param inputvalue string to be processed.
     * @return processed string.
     */
    private String generatedValueFromString(final String inputvalue) {
        return inputvalue.trim()
                .replace(MetLifeConstants.WHITE_SPACE, MetLifeConstants.UNDERSCORE).toLowerCase();

    }

    /**
     *
     * Get priority.
     *  @return get low priority.
     */
    public final int priority() {
        return Constants.LOW_PRIORITY + MLCommonsConstants.TEN;
    }


}
