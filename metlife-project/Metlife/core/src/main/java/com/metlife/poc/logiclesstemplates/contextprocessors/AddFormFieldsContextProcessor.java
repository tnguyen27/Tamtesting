package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.poc.util.JSONUtils;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import com.xumak.base.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * AddFormFieldsContextProcessor.
 *
 * *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/02/17 | Jorge Hernandez       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddFormFieldsContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {

    /**
     * Logger.
     */
    private Logger lOG =
            LoggerFactory.getLogger(AddFormFieldsContextProcessor.class);

    /**
     * RESOURCE_TYPE.
     */
    public static final String RESOURCE_TYPE =
            "MetlifeApp/components/forms/container";
    /**
     * BASE_FIELD_RESOURCE_TYPE.
     */
    public static final String BASE_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/base";
    /**
     * TEXT_FIELD_RESOURCE_TYPE.
     */
    public static final String TEXT_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/text";
    /**
     * SELECT_FIELD_RESOURCE_TYPE.
     */
    public static final String SELECT_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/selection";
    /**
     * PHONE_NUMBER_FIELD_RESOURCE_TYPE.
     */
    public static final String PHONE_NUMBER_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/phone-number";
    /**
     * TEXT_AREA_FIELD_RESOURCE_TYPE.
     */
    public static final String TEXT_AREA_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/text-area";
    /**
     * CHECK_BOX_FIELD_RESOURCE_TYPE.
     */
    public static final String CHECK_BOX_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/check-box";
    /**
     * RADIO_BOX_FIELD_RESOURCE_TYPE.
     */
    public static final String RADIO_BOX_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/radio-box";
    /**
     * BIRTHDAY_FIELD_RESOURCE_TYPE.
     */
    public static final String BIRTHDAY_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/birth-date";
    /**
     * TERMS_AND_CONDITIONS_FIELD_RESOURCE_TYPE.
     */
    public static final String TERMS_AND_CONDITIONS_FIELD_RESOURCE_TYPE =
            "MetlifeApp/components/forms/fields/terms-and-conditions";

    /**
     * FIELDS_PARSYS_NODE_NAME.
     */
    public static final String FIELDS_PARSYS_NODE_NAME = "fields";
    /**
     * FIELDS_KEY.
     */
    public static final String FIELDS_KEY = "fields";
    /**
     * FIELDS_IDS_KEY.
     */
    public static final String FIELDS_IDS_KEY = "ids";
    /**
     * FIELDS_CONFIGURATION_KEY.
     */
    public static final String FIELDS_CONFIGURATION_KEY = "config";
    /**
     * SELECT_PROVIDERS.
     */
    public static final String SELECT_PROVIDERS = "observes";


    @Override
    public final String requiredResourceType() {
        return RESOURCE_TYPE;
    }


    /**
     * Gets the property value from node.
     * @param node The node where the value must be found.
     * @param property The property to find.
     * @return The value found in node or null if not found.
     */
    public final Value getPropertyValue(final Node node,
                                        final String property) {
        try {
            if (node.hasProperty(property)) {
                final Property prop = node.getProperty(property);
                if (prop != null && prop.getValue() != null) {
                    return prop.getValue();
                }
            }
            return null;
        } catch (RepositoryException e) {
            return null;
        }
    }

    /**
     * Get a property from the node as a boolean.
     * @param node The node where the value must be found.
     * @param property The property to find.
     * @return The value found in node or null if not found.
     */
    public final boolean getPropertyAsBoolean(final Node node, final String
            property) {
        try {
            final Value val = getPropertyValue(node, property);
            if (val != null) {
                return val.getBoolean();
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Get a property from the node as a string.
     * @param node The node where the value must be found.
     * @param property The property to find.
     * @return The value found in node or Constants.BLANK if not found.
     */
    public final String getPropertyAsString(final Node node,
                                            final String property) {
        try {
            final Value val = getPropertyValue(node, property);
            if (val != null) {
                return val.getString();
            }
            return Constants.BLANK;
        } catch (Exception e) {
            return Constants.BLANK;
        }

    }

    /**
     * Gets a property from the node.
     * @param node The node where the value must be found.
     * @param property The property to find.
     * @return String with the property value or Constants.BLANK.
     */
    public final String getOptions(final Node node, final String property) {
        try {
            final Value val = getPropertyValue(node, property);
            if (val != null) {
                return val.getString();
            }
            return Constants.BLANK;
        } catch (Exception e) {
            return Constants.BLANK;
        }

    }

    /**
     * Sets the basic properties.
     * @param contentModel The content model.
     * @param fieldNode The field node.
     * @param config The configurations map.
     * @param pathFields The path fields.
     */
    public final void setBasicProperties(final ContentModel contentModel,
                                   final Node fieldNode,
                                   final Map config,
                                   final Map<String, String> pathFields) {
        config.put(MetLifeConstants.FORM_JSON_FIELD_ID, getPropertyAsString(
                fieldNode,
                MetLifeConstants.FORM_WEB_2_LEAD_FIELD));
        config.put(MetLifeConstants.FORM_JSON_VALIDATOR, getPropertyAsString(
                fieldNode,
                MetLifeConstants.FORM_VALIDATOR));
        final boolean isObservesEnabled = getPropertyAsBoolean(fieldNode,
                MetLifeConstants.FORM_OBSERVES_ENABLED);
        config.put(MetLifeConstants.FORM_JSON_HIDDEN, isObservesEnabled);
        final ArrayList<Map<String, Object>> observes = new ArrayList<>();
        if (isObservesEnabled) {
            final Map<String, Object> observe = new HashMap<>();

            final String field = getPropertyAsString(fieldNode,
                    MetLifeConstants.FORM_OBSERVES);

            if (StringUtils.isNotBlank(field) && pathFields.containsKey(
                    field)) {
                observe.put(MetLifeConstants.FORM_JSON_OBSERVES_FIELD,
                        pathFields.get(field));
                final String value = getPropertyAsString(fieldNode,
                        MetLifeConstants.FORM_OBSERVES_VALUE);
                if (StringUtils.isNotBlank(value)) {
                    observe.put(MetLifeConstants.FORM_JSON_OBSERVES_VALUES,
                            new String[]
                            {value});
                }
            }

            if (!observe.isEmpty()) {
                observes.add(observe);
            }


        }
        config.put(MetLifeConstants.FORM_JSON_OBSERVES, observes);

    }

    /**
     * Returns a map with generic fields.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @param tYPE The type of the configuration.
     * @return A map with the configurations.
     */
    public final Map<String, Object> genericField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields,
            final String tYPE) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();
        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE, tYPE);
        return fieldConfig;
    }

    /**
     * Set a text field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> textField(final ContentModel contentModel,
                                         final Resource field,
                                         final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();
        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_TEXT_FIELD);
        return fieldConfig;
    }

    /**
     * Set a text area field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> textAreaField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();
        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_TEXT_AREA_FIELD);
        return fieldConfig;
    }

    /**
     * Set a phone number field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> phoneNumberField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();
        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_PHONE_NUMBER_FIELD);
        return fieldConfig;
    }

    /**
     * Returns the select options.
     * @param node The node to find the select.
     * @param property The property to find.
     * @return An arrayList with the options.
     */
    public final ArrayList<Map> getSelectOptions(final Node node, final String
            property) {
        final ArrayList<Map> options = new ArrayList<>();
        try {
            for (String item : PropertyUtils.asStrings(node.getProperty(
                    MetLifeConstants.FORM_OPTIONS))) {
                final Map itemAttrs = JSONUtils.jsonStringObjectToMap(item);
                try {
                    options.add(Collections.singletonMap(itemAttrs.get(
                            Constants.TEXT).toString(),
                            itemAttrs.get(MetLifeConstants.VALUE).toString()));
                } catch (Exception e) {
                    lOG.debug("> Error parsing option {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            lOG.error("error {}", e.getMessage());
        }
        return options;
    }

    /**
     * Set a select field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> selectField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();

        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        //Options
        final Map<String, Object> options = Maps.newHashMap();
        options.put(MetLifeConstants.FORM_JSON_OPTIONS_LABEL,
                getPropertyAsString(fieldNode,
                MetLifeConstants.FORM_OPTIONS_LABEL));
        options.put(MetLifeConstants.FORM_JSON_OPTIONS_NAME,
                getPropertyAsString(fieldNode,
                MetLifeConstants.FORM_OPTIONS_NAME));
        options.put(MetLifeConstants.FORM_JSON_OPTIONS,
                getSelectOptions(fieldNode,
                MetLifeConstants.FORM_OPTIONS));


        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_SELECT_FIELD);
        fieldConfig.put(MetLifeConstants.FORM_JSON_OPTIONS,
                new Map[] {options});
        return fieldConfig;
    }

    /**
     * Set a check box field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> checkBoxField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();

        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_CHECK_BOX_FIELD);
        fieldConfig.put(MetLifeConstants.FORM_JSON_OPTIONS,
                getSelectOptions(fieldNode, MetLifeConstants.FORM_OPTIONS));
        return fieldConfig;
    }

    /**
     * Set a radio box field.
     * @param contentModel The content model.
     * @param field The Resource.
     * @param pathFields A map with the path fields.
     * @return A map with the configurations.
     */
    public final Map<String, Object> radioBoxField(
            final ContentModel contentModel,
            final Resource field,
            final Map<String, String> pathFields) {
        final Map<String, Object> fieldConfig = Maps.newHashMap();

        final Node fieldNode = field.adaptTo(Node.class);
        setBasicProperties(contentModel, fieldNode, fieldConfig, pathFields);
        fieldConfig.put(MetLifeConstants.FORM_JSON_FIELD_TYPE,
                MetLifeConstants.FORM_JSON_TYPE_RADIO_BOX_FIELD);
        fieldConfig.put(MetLifeConstants.FORM_JSON_OPTIONS,
                getSelectOptions(fieldNode, MetLifeConstants.FORM_OPTIONS));
        return fieldConfig;
    }

    /**
     * Returns a Map with the path fields of the parsys child node.
     * @param request The SlingHttpServletRequest
     * @return Map with the path fields
     */
    public final Map<String, String> pathFields(final SlingHttpServletRequest
                                                  request) {
        final Resource fields = request.getResource().getChild(
                FIELDS_PARSYS_NODE_NAME);
        final Map<String, String> pathFields = Maps.newHashMap();

        final Iterator<Resource> iterator = fields.listChildren();
        while (iterator.hasNext()) {
            final Resource child = iterator.next();
            if (child.isResourceType(BASE_FIELD_RESOURCE_TYPE)) {
                final String fieldId = getPropertyAsString(child.adaptTo(Node
                        .class), MetLifeConstants.FORM_WEB_2_LEAD_FIELD);
                pathFields.put(child.getPath(), fieldId);
            }
        }
        return pathFields;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        lOG.debug("> AddFormFields");
        final Map<String, Object> configuration = Maps.newHashMap();
        final Resource fields = request.getResource().getChild(
                FIELDS_PARSYS_NODE_NAME);
        final Iterator<Resource> iterator = fields.listChildren();
        final Gson gsonInstance = new Gson();
        final Map<String, String> pathFields = pathFields(request);
        final ArrayList<Object> formFields = new ArrayList<>();
        final ArrayList<Object> formFieldIds = new ArrayList<>();
        final  ArrayList<Map<String, String>> observes = new ArrayList<>();
        while (iterator.hasNext()) {
            final Resource child = iterator.next();
            if (child.isResourceType(BASE_FIELD_RESOURCE_TYPE)) {
                final String fieldId = getPropertyAsString(child.adaptTo(Node
                        .class), MetLifeConstants.FORM_WEB_2_LEAD_FIELD);
                if (StringUtils.isNotBlank(fieldId)) {
                    formFieldIds.add(fieldId);
                }
                if (child.isResourceType(TEXT_FIELD_RESOURCE_TYPE)) {
                    final Map field = textField(contentModel, child,
                            pathFields);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(SELECT_FIELD_RESOURCE_TYPE)) {
                    final Map field = selectField(contentModel, child,
                            pathFields);

                    final String title = getPropertyAsString(child.adaptTo(Node
                            .class), MetLifeConstants.FORM_TITLE);
                    final Map<String, String> ops = new HashMap<>();
                    ops.put(Constants.TEXT, title);
                    ops.put(MetLifeConstants.VALUE, child.getPath());
                    observes.add(ops);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(
                        PHONE_NUMBER_FIELD_RESOURCE_TYPE)) {
                    final Map field = phoneNumberField(contentModel, child,
                            pathFields);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(
                        TEXT_AREA_FIELD_RESOURCE_TYPE)) {
                    final Map field = textAreaField(contentModel, child,
                            pathFields);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(
                        CHECK_BOX_FIELD_RESOURCE_TYPE)) {
                    final Map field = checkBoxField(contentModel, child,
                            pathFields);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(BIRTHDAY_FIELD_RESOURCE_TYPE)) {
                    final Map field = genericField(contentModel, child,
                            pathFields,
                            MetLifeConstants.FORM_JSON_TYPE_BIRTH_DATE_FIELD);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(
                        RADIO_BOX_FIELD_RESOURCE_TYPE)) {
                    final Map field = radioBoxField(contentModel, child,
                            pathFields);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                } else if (child.isResourceType(
                        TERMS_AND_CONDITIONS_FIELD_RESOURCE_TYPE)) {
                    final  Map field = genericField(contentModel, child,
                            pathFields,
                        MetLifeConstants.FORM_JSON_TYPE_TERMS_CONDITIONS_FIELD);
                    if (!field.isEmpty()) {
                        formFields.add(field);
                    }
                }


            }
        }
        String formType = MetLifeConstants.DEFAULT_FORM_TYPE;
        try {
            formType = request.getResource().getParent().adaptTo(Node.class)
                    .getProperty(MetLifeConstants.FORM_TYPE).getValue()
                    .getString();
        } catch (Exception e) {
            lOG.error("Error getting form type {}", e.getMessage());
        }
        if (StringUtils.isNotEmpty(formType)) {
            configuration.put(MetLifeConstants.FORM_JSON_TYPE, formType);
        }

        configuration.put(MetLifeConstants.FORM_JSON_FIELDS, formFields);
        contentModel.set(FIELDS_KEY + Constants.DOT + FIELDS_IDS_KEY,
                StringUtils.join(
                formFieldIds, MetLifeConstants.COMMA));
        contentModel.set(FIELDS_KEY + Constants.DOT + FIELDS_CONFIGURATION_KEY,
                gsonInstance.toJson(configuration));
        contentModel.set(FIELDS_KEY + Constants.DOT
                + MetLifeConstants.FORM_TYPE, formType);
        contentModel.set(Constants.CONTENT + Constants.DOT
                + SELECT_PROVIDERS, observes);
    }
}

