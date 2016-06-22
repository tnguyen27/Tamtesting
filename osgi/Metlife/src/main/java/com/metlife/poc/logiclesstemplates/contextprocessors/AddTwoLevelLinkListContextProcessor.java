package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.poc.util.JSONUtils;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**.
 * AddTwoLevelCuratedPathListContextProcessor
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/01/07 | Pablo Alecio    | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class AddTwoLevelLinkListContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * PATH_REFS_CONFIGURATION_PROPERTY_NAMES.
     */
    public static final String PATH_REFS_CONFIGURATION_PROPERTY_NAMES =
            "twoLevelCuratedPathRefsKeyNames";

    /**
     * DEFAULT_PATH_REFS_KEY_NAME.
     */
    public static final String DEFAULT_PATH_REFS_KEY_NAME = "content.pathRefs";
    /**
     * LINKS.
     */
    public static final String LINKS = "links";
    /**
     * TOP.
     */
    public static final String MAIN = "main";
    /**
     * MEGA_MENU_RESOUCE_NAME.
     */
    public static final String MEGA_MENU_RESOUCE_NAME = "mega-menu";

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public final boolean mustExist() {
        return false;
    }

    /**
     * Get required property names.
     * @return Set of strings.
     */
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(PATH_REFS_CONFIGURATION_PROPERTY_NAMES);
    }

    /**
     * Looks for the pathRefs key name under config. If not found, gets the
     * default one
     *
     * @param resource the request resource
     * @return the name of the key where the traversed list base path is stored
     * @throws Exception throws exception
     */
    private Collection<String> getPathRefListKeyNames(final Resource
                                                              resource)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(
                resource.getResourceType());
        Collection<String> configurationPathRefListKeyName =
                configuration.asStrings(PATH_REFS_CONFIGURATION_PROPERTY_NAMES);
        if (configurationPathRefListKeyName.isEmpty()) {
            configurationPathRefListKeyName =
                    Lists.newArrayList(DEFAULT_PATH_REFS_KEY_NAME);
        }
        return configurationPathRefListKeyName;
        /*
        return configurationPathRefListKeyName.isEmpty()
                ?
                Lists.newArrayList(DEFAULT_PATH_REFS_KEY_NAME)
                : configurationPathRefListKeyName;
                */
    }

    /**
     * get link lists.
     * @param jsonObject JSON object
     * @return List of maps.
     */
    private List<Map> getLinkList(final Object jsonObject) {
        final List<Map> linkList =
                JSONUtils.jsonStringObjectToMapList(jsonObject);

        for (Map link : linkList) {
            if (link.containsKey(LINKS)) {
                link.put(LINKS, JSONUtils.jsonStringObjectToMapList(link.get(
                        LINKS)));
            }
        }
        return linkList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        final Resource resource = request.getResource();
        final int three = 3;
        final int four = 4;
        for (String pathRefListKeyName : getPathRefListKeyNames(resource)) {
            final Object pathRefsJsonObject =
                    contentModel.get(pathRefListKeyName);
            final List<Map> linkList = getLinkList(pathRefsJsonObject);


            //Due to MetLife's markup, we need to always have 3 elements on
            // the top section and 4 on the bottom section
            //even if some of them are empty.
            if (resource.getName().equals(MEGA_MENU_RESOUCE_NAME)) {
                int max = four;
                if (pathRefListKeyName.contains(MAIN)) {
                    max = three;
                }
                //final int max = pathRefListKeyName.contains(TOP) ?
                // three : four;
                while (linkList.size() < max) {
                    linkList.add(Maps.newHashMap());
                }
            } else {
                linkList.add(Maps.newHashMap());
            }

            contentModel.set(pathRefListKeyName, linkList);
        }
    }
}

