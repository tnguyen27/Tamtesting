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
public class AddHashContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {


    /**
     * ADD_HASH_PROPERTY.
     */
    private static final String ADD_HASH_PROPERTY = "xk_AddHashProperty";
    /**
     * HASHCODE_PROPERTY.
     */
    private static final String HASHCODE_PROPERTY = "hashcode";
    /**
     * FORM_PROPERTY.
     */
    private static final String FORM_PROPERTY = "form";

    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - 1;
    } //must be executed after AddDeserializedJsonListContextProcessor

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(ADD_HASH_PROPERTY);
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


        final String propertyPath = contentModel.getAsString(
                Constants.CONFIG_PROPERTIES_KEY
                        + Constants.DOT + ADD_HASH_PROPERTY);

        if (contentModel.has(propertyPath)) {
            if (contentModel.get(propertyPath) instanceof String) {
                final String propertyString = contentModel.getAsString(
                        propertyPath);

                contentModel.set(Constants.CONTENT + Constants.DOT
                        + HASHCODE_PROPERTY, String
                        .valueOf(propertyString.hashCode()));

            } else {
                final ArrayList<HashMap<String, String>> propertyArray =
                        (ArrayList<HashMap<String, String>>) Utils
                                .getPropertyAsList(contentModel, propertyPath);
                for (HashMap<String, String> element : propertyArray) {
                    if (element.containsKey(FORM_PROPERTY)) {
                        element.put(HASHCODE_PROPERTY, String.valueOf(element
                                .get(FORM_PROPERTY).hashCode()));
                    }

                }

            }


        }
    }

}


