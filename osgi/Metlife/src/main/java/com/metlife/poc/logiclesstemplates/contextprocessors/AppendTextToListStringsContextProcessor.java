package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.xumak.base.Constants;

/**
 * Created by j.amorataya on 2/16/16.
 */
@Component
@Service
public class AppendTextToListStringsContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {
    /**
     * STRING_TO_APPEND_PROPERTY_NAME.
     */
    private static final String STRING_TO_APPEND_PROPERTY_NAME =
            "xk_StringToAppend";
    /**
     * ARRAY_PATH_PROPERTY.
     */
    private static final String ARRAY_PATH_PROPERTY = "xk_ArrayPathProperty";


    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 1;
    } //must be executed after AddDeserializedJsonListContextProcessor

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(STRING_TO_APPEND_PROPERTY_NAME);
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }


    @Override
    public final void process(
            final SlingHttpServletRequest slingHttpServletRequest,
            final ContentModel contentModel) throws Exception {
        final String tagListPropertyName =
                contentModel.getAsString(Constants.CONFIG_PROPERTIES_KEY
                        + Constants.DOT
                        + STRING_TO_APPEND_PROPERTY_NAME);
        if (contentModel.has(Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                + ARRAY_PATH_PROPERTY)) {
            String propertyPath = contentModel.getAsString(
                    Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                            + ARRAY_PATH_PROPERTY);
            final String arrayProperty = propertyPath.substring(0, propertyPath
                    .indexOf(':'));
            propertyPath = propertyPath.substring(propertyPath.indexOf(':')
                    + 1, propertyPath.length());

            if (contentModel.has(arrayProperty)) {
                final ArrayList<HashMap<String, String>> propertyArray =
                        (ArrayList<HashMap<String, String>>) Utils
                                .getPropertyAsList(contentModel, arrayProperty);

                for (HashMap<String, String> element : propertyArray) {
                    if (element.containsKey(propertyPath)) {
                        final String path = element.get(propertyPath)
                                + tagListPropertyName;
                        element.put(propertyPath, path);
                    }
                }
            }


        }
    }
}
