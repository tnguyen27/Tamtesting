package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;


/**
 * MultiLinksContentProcessor.
 * Used to build a link list with their properties (text,link,target).The
 * properties are stored in the global dialog
 * (/apps/MetlifeApp/components/page/htmlbase/global_dialog/items/items
 * /countries-and-languages)
 *
 * The component that use this processor is the languge_selector:
 * Component path:
 * /apps/MetlifeApp/components/section/language-selector
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 01/14/2016 | jpol@xumak.com  | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class MultiLinksContentProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * MULTI_LINKS_CONFIG_PROPERTY_NAMES.
     */
    public static final String MULTI_LINKS_CONFIG_PROPERTY_NAMES =
            "multiLinksPathRefsKeyNames";
    /**
     * DEFAULT_PATH_REFS_KEY_NAME.
     */
    public static final String DEFAULT_PATH_REFS_KEY_NAME = "content.pathRefs";
    /**
     * GLOBAL_LINK_TEXT_KEY.
     */
    public static final String GLOBAL_LINK_TEXT_KEY = "global.langSelLinkText";
    /**
     * GLOBAL_LINK_TYPE_KEY.
     */
    public static final String GLOBAL_LINK_TYPE_KEY = "global.langSelLinkType";
    /**
     * GLOBAL_INTERNAL_LINK_KEY.
     */
    public static final String GLOBAL_INTERNAL_LINK_KEY =
            "global.langSelInternalLink";
    /**
     * GLOBAL_EXTERNAL_LINK_KEY.
     */
    public static final String GLOBAL_EXTERNAL_LINK_KEY =
            "global.langSelExternalLink";
    /**
     * GLOBAL_TARGET_LINK_KEY.
     */
    public static final String GLOBAL_TARGET_LINK_KEY =
            "global.langSelTargetLink";
    /**
     * GLOBAL_LANGUAGE_LIST_KEY.
     */
    public static final String GLOBAL_LANGUAGE_LIST_KEY = "global.languageList";
    /**
     * INTERNAL.
     */
    public static final String INTERNAL = "internal";
    /**
     * EXTERNAL.
     */
    public static final String EXTERNAL = "external";
    /**
     * TITLE.
     */
    public static final String TITLE = "title";
    /**
     * LINK.
     */
    public static final String LINK = "link";
    /**
     * TARGET.
     */
    public static final String TARGET = "target";
    /**
     * HTML.
     */
    public static final String HTML = ".html";
    /**
     * SHARP.
     */
    public static final String SHARP = "#";

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

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
        return Sets.newHashSet(MULTI_LINKS_CONFIG_PROPERTY_NAMES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        final Resource resource = request.getResource();

        final ArrayList<String> linkTextList = new ArrayList<>();
        final ArrayList<String> linkTypeList = new ArrayList<>();
        final ArrayList<String> internaLinkList = new ArrayList<>();
        final ArrayList<String> externalLinkList = new ArrayList<>();
        final ArrayList<String> tagetLinkList = new ArrayList<>();

        for (String pathRefListKeyName : getPathRefListKeyNames(resource)) {

            Object pathRefsObject = contentModel.get(pathRefListKeyName);
            if (pathRefsObject == null) {
                pathRefsObject = "";
            }
            //pathRefsObject = (pathRefsObject == null) ? "" : pathRefsObject;

            if (pathRefListKeyName.equals(GLOBAL_LINK_TEXT_KEY)) {
                setPropertiesLikeArray(pathRefsObject, linkTextList);
            } else if (pathRefListKeyName.equals(GLOBAL_LINK_TYPE_KEY)) {
                setPropertiesLikeArray(pathRefsObject, linkTypeList);
            } else if (pathRefListKeyName.equals(GLOBAL_INTERNAL_LINK_KEY)) {
                setPropertiesLikeArray(pathRefsObject, internaLinkList);
            } else if (pathRefListKeyName.equals(GLOBAL_EXTERNAL_LINK_KEY)) {
                setPropertiesLikeArray(pathRefsObject, externalLinkList);
            } else if (pathRefListKeyName.equals(GLOBAL_TARGET_LINK_KEY)) {
                setPropertiesLikeArray(pathRefsObject, tagetLinkList);
            }
        }

        final ArrayList<HashMap> linksList = new ArrayList<HashMap>();

        if (linkTextList.size() > 0) {
            for (int i = 0; i < linkTextList.size(); i++) {
                final HashMap<String, Object> linkMap =
                        new HashMap<String, Object>();

                final String linkType = linkTypeList.get(i);
                final String internalLink = internaLinkList.get(i);
                final String externalLink = externalLinkList.get(i);
                String link = "";
                if (null != linkType) {
                    if (linkType.equals(INTERNAL)) {
                        if (internalLink.isEmpty()) {
                            link = SHARP;
                        } else {
                            link = internalLink.concat(HTML);
                        }
                        /*
                        link = (internalLink.isEmpty()) ? SHARP
                                :
                                internalLink.concat(HTML);
                                */
                    } else if (linkType.equals(EXTERNAL)) {
                        if (externalLink.isEmpty()) {
                            link = SHARP;
                        } else {
                            link = externalLink;
                        }
                        /*
                        link = (externalLink.isEmpty()) ? SHARP : externalLink;
                        */
                    }
                } else {
                    link = SHARP;
                }

                linkMap.put(TITLE, linkTextList.get(i));
                linkMap.put(LINK, link);
                linkMap.put(TARGET, tagetLinkList.get(i));
                linksList.add(linkMap);
            }
        }

        contentModel.set(GLOBAL_LANGUAGE_LIST_KEY, linksList);
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
                configuration.asStrings(MULTI_LINKS_CONFIG_PROPERTY_NAMES);
        /*
        return configurationPathRefListKeyName.isEmpty()
                ?
                Lists.newArrayList(DEFAULT_PATH_REFS_KEY_NAME)
                :
                configurationPathRefListKeyName;
                */
        if (configurationPathRefListKeyName.isEmpty()) {
            configurationPathRefListKeyName =
                    Lists.newArrayList(DEFAULT_PATH_REFS_KEY_NAME);
        }
        return configurationPathRefListKeyName;
    }

    /**
     * Method used to setting the properties in a Array Object.
     * @param object Object.
     * @param list ArrayList<String>.
     */
    public final void setPropertiesLikeArray(final Object object,
                                       final ArrayList<String> list) {

        if (object.toString().startsWith("[")) {
            final String[] propValueList = object.toString().replace("[", "")
                    .replace("]", "").split(",");
            for (String s : propValueList) {
                list.add(s.trim());
            }
        } else {
            list.add(object.toString());
        }
    }


}
