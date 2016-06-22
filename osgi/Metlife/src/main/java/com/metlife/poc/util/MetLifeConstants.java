package com.metlife.poc.util;

/**
 * Created by palecio on 1/4/16.
 */

import com.xumak.base.Constants;

/**
 * MetLifeConstants class.
 */
public final class MetLifeConstants {
    /**
     * MetLife constants.
     */
    private MetLifeConstants() {

    }

    /**
     * Ampersand symbol - .
     */
    public static final String AMPERSAND = "&";
    /**
     * Equal symbol.
     */
    public static final String EQUALS = "=";
    /**
     * UTF-8 Charset.
     */
    public static final String UTF8_CHARSET = "UTF-8";
    /**
     * TEXT/HTML content type.
     */
    public static final String TEXT_HTML = "text/html";

    /* BOOLEAN VALUES */
    /**
     * And operator.
     */
    public static final String AND = "and";
    /**
     * Or operator.
     */
    public static final String OR = "or";
    /**
     * EqualTo literal.
     */
    public static final String EQUAL_TO_RESULT = "EqualTo_";

    /**
     * JSON Content Type.
     */
    public static final String JSON_CONTENT_TYPE = "application/json";
    /**
     * JSON literal.
     */
    public static final String JSON = "json";
    /**
     * HTTP POST Method.
     */
    public static final String HTTP_POST = "POST";
    /**
     * HTTP GET Method.
     */
    public static final String HTTP_GET = "GET";
    /**
     * HTTP DELETE Method.
     */
    public static final String HTTP_DELETE = "DELETE";
    /**
     * Content Type Header.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Form URL Encoded Content Type.
     */
    public static final String APPLICATION_FORM_URLENCODED =
            "application/x-www-form-urlencoded";
    /**
     * HTTP literal.
     */
    public static final String HTTP_PROTOCOL = "http";
    /**
     * Escaped dot literal.
     */
    public static final String ESCAPE_DOT = "\\.";
    /**
     * Slash literal.
     */
    public static final char SLASH_CHAR = '/';
    /**
     * Not Operator.
     */
    public static final String NOT = "!";
    /**
     * Underscore literal.
     */
    public static final String UNDERSCORE = "_";
    /**
     * Dash Literal.
     */
    public static final String DASH = "-";
    /**
     * Colon literal.
     */
    public static final String COLON = ":";
    /**
     * Semi colon literal.
     */
    public static final String SEMI_COLON = ";";
    /**
     * White space.
     */
    public static final String WHITE_SPACE = " ";
    /**
     * Number sign.
     */
    public static final String NUMBER_SIGN = "#";
    /**
     * X literal.
     */
    public static final String X_LETTER = "x";
    /**
     * Escaped quotes.
     */
    public static final String QUOTES = "\"";
    /**
     * Double colon.
     */
    public static final String DOUBLE_COLON = COLON + COLON;
    /**
     * At symbol.
     */
    public static final String AT = "@";
    /**
     * Shift right.
     */
    public static final String SHIFT_RIGHT = ">>";
    /**
     * Equal operator.
     */
    public static final String EQUAL_TO_OPERATOR = "==";
    /**
     * Arrow operator.
     */
    public static final String ARROW_OPERATOR = "->";
    /**
     * Comma.
     */
    public static final String COMMA = ",";


    /**
     * CQ Page Node Property.
     */
    public static final String CQ_PAGE = "cq:Page";

    /* SPOOLED IMAGE PATH CONTEXT PROCESSOR */
    /**
     * Global Spooled Images .xk property.
     */
    public static final String GLOBAL_IMAGE_RESOURCES_KEY =
            "xk_spoolGlobalImages";
    /**
     * Design Spooled Images .xk property.
     */
    public static final String DESIGN_IMAGE_RESOURCES_KEY =
            "xk_spoolDesignImages";
    /**
     * Content Spooled Images .xk property.
     */
    public static final String CONTENT_IMAGE_RESOURCES_KEY =
            "xk_spoolContentImages";
    /**
     * Page Spooled Images .xk property.
     */
    public static final String PAGE_IMAGE_RESOURCES_KEY = "xk_spoolPageImages";
    /**
     * Spooled literal.
     */
    public static final String SPOOLED = "spooled";
    /**
     * Default design path.
     */
    public static final String DEFAULT_DESIGN_PATH = "/etc/designs/default";
    /**
     * CQ Design Path  Node Property.
     */
    public static final String CQ_DESIGN_PATH_KEY = "cq:designPath";
    /**
     * Renditions .xk property.
     */
    public static final String RENDITIONS = "xk_renditions";
    /**
     * Original literal.
     */
    public static final String ORIGINAL = "original";

    /**
     * Global Context.
     */
    public static final int GLOBAL_CONTEXT = 0;
    /**
     * Design Context.
     */
    public static final int DESIGN_CONTEXT = 1;
    /**
     * Content Context.
     */
    public static final int CONTENT_CONTEXT = 2;
    /**
     * Page Context.
     */
    public static final int PAGE_CONTEXT = 3;

    /* MetLifeConstants for html rewriter */
    /**
     * Slash Content literal.
     */
    public static final String SLASH_CONTENT = Constants.SLASH + Constants
            .CONTENT;
    /**
     * HTML extension.
     */
    public static final String HTML_EXTENSION = ".html";

    /* XK_CONFIG Properties */
    /**
     * XK Config property.
     */
    public static final String XK_EVALUATE_EQUALS_CONDITION =
            "xk_evaluateEqualsCondition";
    /**
     * XK Config property.
     */
    public static final String XK_EVALUATE_CONDITIONS =
            "xk_evaluateAndCondition";
    /**
     * XK Config property.
     */
    public static final String XK_EVALUATE_OR_CONDITIONS =
            "xk_evaluateOrCondition";

    /* Find an X */
    /**
     * Find an X property.
     */
    public static final String PROXIMITY = "proximity";
    /**
     * Find an X property.
     */
    public static final String PARAM_DELIMITER = "=";
    /**
     * Find an X property.
     */
    public static final String LOCATIONS = "locations";
    /**
     * Find an X property.
     */
    public static final String NEAR_LOCATIONS = "nearLocations";
    /**
     * Find an X property.
     */
    public static final String UNITS = "units";
    /**
     * Find an X property.
     */
    public static final String LATITUDE = "latitude";
    /**
     * Find an X property.
     */
    public static final String LONGITUDE = "longitude";
    /**
     * Find an X property.
     */
    public static final String RADIUS = "radius";
    /**
     * Find an X property.
     */
    public static final String DISTANCE = "distance";
    /**
     * Find an X property.
     */
    public static final String SPECIALTY = "specialty";
    /**
     * Find an X property.
     */
    public static final String SPECIALTIES = "specialties";
    /**
     * Find an X property.
     */
    public static final String FACILITIES = "facilities";

    /**
     * Node property.
     */
    public static final String KEY = "key";
    /**
     * Node property.
     */
    public static final String VALUE = "value";

    /**
     * Node property.
     */
    public static final String RESULTS = "results";
    /**
     * Node property.
     */
    public static final String RESULT = "result";
    /**
     * Node property.
     */
    public static final String RESPONSE = "response";
    /**
     * Node property.
     */
    public static final String DOCS = "docs";
    /**
     * Node property.
     */
    public static final String PRODUCTS = "products";
    /**
     * Node property.
     */
    public static final String PRODUCT = "product";
    /**
     * Node property.
     */
    public static final String ROOT_PATH_KEY = "rootPath";
    /**
     * Node property.
     */
    public static final String DATE_KEY = "date";
    /**
     * Node property.
     */
    public static final String URL_KEY = "url";
    /**
     * Node property.
     */
    public static final String TITLE_KEY = "title";
    /**
     * Node property.
     */
    public static final String ID_KEY = "id";
    /**
     * Node property.
     */
    public static final String CONTENT_KEY = "content";
    /**
     * Node property.
     */
    public static final String CONTENT_LENGTH_KEY = "contentLength";
    /**
     * Node property.
     */
    public static final String TIMESTAMP_KEY = "tstamp";
    /**
     * Node property.
     */
    public static final String LAST_MODIFIED_KEY = "lastModified";
    /**
     * Node property.
     */
    public static final String TYPE_KEY = "type";
    /**
     * Node property.
     */
    public static final String START_KEY = "start";
    /**
     * Node property.
     */
    public static final String LIMIT_KEY = "limit";
    /**
     * Node property.
     */
    public static final String DC_TITLE = "dc:title";
    /**
     * Node property.
     */
    public static final String DC_DESCRIPTION = "dc:description";
    /**
     * Node property.
     */
    public static final String CUSTOM_URL_KEY = "custom_s_url";
    /**
     * Node property.
     */
    public static final String DESCRIPTION_KEY = "custom_t_description";
    /**
     * Node property.
     */
    public static final String CATEGORY_NAME_KEY = "custom_s_category_name";
    /**
     * Node property.
     */
    public static final String CATEGORY_TITLE_KEY = "custom_s_category_title";
    /**
     * Node property.
     */
    public static final String DC_FORMAT = "dc:format";
    /**
     * Node property.
     */
    public static final String FORM_TYPE_KEY = "custom_s_form_type";
    /**
     * Node property.
     */
    public static final String DAM_SIZE = "dam:size";
    /**
     * Node property.
     */
    public static final String SIZE_KEY = "custom_i_size";
    /**
     * Node property.
     */
    public static final String NAMESPACE_KEY = "namespace";
    /**
     * Node property.
     */
    public static final String LIST = "list";
    /**
     * Node property.
     */
    public static final String ENCODED_ID = "encodedId";

    /**
     * Node property.
     */
    public static final String DOCS_KEY = "docs";
    /**
     * Node property.
     */
    public static final String NUM_FOUND_KEY = "numFound";

    //JCR
    /**
     * Node property.
     */
    public static final String LAST_MODIFIED = "cq:lastModified";
    //FORM LIBRARY
    /**
     * Node property.
     */
    public static final String METADATA = "metaData";
    /**
     * Node property.
     */
    public static final String CATEGORY_TITLE = "category_title";
    /**
     * Node property.
     */
    public static final String CATEGORY_DESCRIPTION = "category_description";
    /**
     * Node property.
     */
    public static final String DOWNLOAD_TEXT = "download_text";
    /**
     * Node property.
     */
    public static final String OTHER_FORMS_TITLE = "other_forms_title";
    /**
     * Node property.
     */
    public static final String OTHER_FORMS_DESCRIPTION =
            "other_forms_description";
    /**
     * Node property.
     */
    public static final String OTHER_FORMS_DETAILS = "other_forms_details";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_URL = "file_url";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_TYPE = "file_type";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_SIZE = "file_size";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_TITLE = "file_title";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_DESCRIPTION = "file_description";
    /**
     * Node property.
     */
    public static final String FRM_LIB_FILE_CATEGORY_TITLE =
            "file_category_title";
    //COMPONENTS PATHS
    /**
     * Newsroom resource type.
     */
    public static final String NEWS_ROOM_ARCHIVE_RESOURCE_TYPE =
            "MetlifeApp/components/section/news-room-archive";


    //FORMS
    /**
     * FORM node property.
     */
    public static final String FORM_TYPE = "formType";
    /**
     * FORM node property.
     */
    public static final String DEFAULT_FORM_TYPE = "contact-image";
    /**
     * FORM node property.
     */
    public static final String FORM_TITLE = "title";
    /**
     * FORM node property.
     */
    public static final String FORM_PLACEHOLDER = "placeholder";
    /**
     * FORM node property.
     */
    public static final String FORM_REQUIRED = "required";
    /**
     * FORM node property.
     */
    public static final String FORM_VALID_TYPE = "validType";
    /**
     * FORM node property.
     */
    public static final String FORM_VALIDATOR = "validator";
    /**
     * FORM node property.
     */
    public static final String FORM_HIDDEN = "hidden";
    /**
     * FORM node property.
     */
    public static final String FORM_OBSERVES_ENABLED = "observesEnabled";
    /**
     * FORM node property.
     */
    public static final String FORM_OPTIONS = "options";
    /**
     * FORM node property.
     */
    public static final String FORM_OPTIONS_LABEL = "optionsLabel";
    /**
     * FORM node property.
     */
    public static final String FORM_OPTIONS_NAME = "optionsName";
    /**
     * FORM node property.
     */
    public static final String FORM_OBSERVES = "observes";
    /**
     * FORM node property.
     */
    public static final String FORM_OBSERVES_VALUE = "observesValue";
    /**
     * FORM node property.
     */
    public static final String FORM_WEB_2_LEAD_FIELD = "web2LeadField";


    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE = "type";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_VALIDATOR = "validator";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_FIELD_TYPE = "type";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_FIELDS = "fields";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_FIELD_ID = "id";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_HIDDEN = "hidden";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OPTIONS = "options";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OPTIONS_LABEL = "label";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OPTIONS_NAME = "name";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OBSERVES_FIELD = "field";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OBSERVES_VALUES = "values";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_OBSERVES = "observes";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_TEXT_FIELD = "text";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_SELECT_FIELD = "select";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_PHONE_NUMBER_FIELD = "phNo";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_TEXT_AREA_FIELD = "textArea";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_CHECK_BOX_FIELD = "checkBox";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_BIRTH_DATE_FIELD = "dob";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_RADIO_BOX_FIELD = "radio";
    /**
     * FORM JSON property.
     */
    public static final String FORM_JSON_TYPE_TERMS_CONDITIONS_FIELD =
            "TermsandconditionsField";
    /**
     * Three.
     */
    public static final int THREE = 3;

}
