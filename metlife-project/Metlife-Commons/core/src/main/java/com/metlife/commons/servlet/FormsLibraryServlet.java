package com.metlife.commons.servlet;

import com.day.cq.dam.api.Asset;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.commons.util.FormsLibraryConstants;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**.
 * FormsLibraryServlet
 *
 * Receives a category identifier as a request parameter and returns
 * a list with the files contained in the configured folder path
 * in the content model.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/08/23 | Diego Tello     | Initial creation
 * 2.0     | 2016/08/29 | Diego Tello     | Get parameters from query parameters
 *                                        | instead of getting them from selectors
 * ----------------------------------------------------------------------------
 */
@Component
@Service
@Properties({
        @Property(name = "service.description", value = "MetLife Forms Library Servlet"),
        @Property(name = "sling.servlet.selectors", value = "results"),
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.resourceTypes", value = {"metlifeCommons/components/section/forms-library"})
        })
public class FormsLibraryServlet extends SlingSafeMethodsServlet {

    /**
     * Logger.
     */
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FormsLibraryServlet.class);
    /**
     * START.
     */
    private static final String START = "start";
    /**
     * PRODUCTS.
     */
    private static final String PRODUCTS = "products";
    /**
     * DOWNLOAD_TEXT.
     */
    private static final String DOWNLOAD_TEXT = "download_text";
    /**
     * METADATA.
     */
    private static final String METADATA = "metaData";
    /**
     * FORMS_PATH.
     */
    private static final String FORMS_PATH = "content.formsPath";
    /**
     * DOCS.
     */
    private static final String DOCS = "docs";
    /**
     * NUM_FOUND.
     */
    private static final String NUM_FOUND = "numFound";
    /**
     * RESPONSE.
     */
    private static final String RESPONSE = "response";
    /**
     * CONTENT_DOWNLOAD_TEXT.
     */
    private static final String CONTENT_DOWNLOAD_TEXT = "content.downloadText";


    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        final RequestParameter productParameter = request.getRequestParameter(PRODUCTS);
        final Map<String, Object> customResponse = Maps.newHashMap();
        final Map<String, Object> customResponseData = Maps.newHashMap();
        customResponseData.put(START, 0);
        if (productParameter != null) {
            final String product = productParameter.getString();
            final TemplateContentModel contentModel =
                    (TemplateContentModel) request.getAttribute(
                    TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);

            final LinkedList<Map<String, Object>> metadata = new LinkedList();
            final Map<String, Object> metadataWrapper = Maps.newHashMap();
            metadataWrapper.put(DOWNLOAD_TEXT, contentModel.getAsString(CONTENT_DOWNLOAD_TEXT));
            metadata.add(metadataWrapper);
            customResponseData.put(METADATA, metadata);

            if (contentModel.has(FORMS_PATH)) {
                final String formsPath = (String) contentModel.get(FORMS_PATH);
                final Resource formsDir = request.getResourceResolver().getResource(formsPath);
                final Iterator<Resource> children = formsDir.listChildren();
                Resource child = null;
                while (children.hasNext()) {
                    child = children.next();
                    if (child.getName().equals(product)) {
                        final Resource categoryChildJcrContent = child.getChild(
                                Constants.JCR_CONTENT);
                        if (categoryChildJcrContent != null) {
                            final ValueMap categoryMap = categoryChildJcrContent.adaptTo(ValueMap.class);
                            final Iterator<Resource> categoryChildren = child.listChildren();
                            final LinkedList<Map<String, Object>> categoryFiles = new LinkedList();
                            Resource file = null;
                            while (categoryChildren.hasNext()) {
                                file = categoryChildren.next();
                                if (file.getResourceType().equals(FormsLibraryConstants.DAM_ASSET)) {
                                    final Resource fileJcrContent = file.getChild(
                                            Constants.JCR_CONTENT);
                                    if (fileJcrContent != null) {
                                        final Resource fileMetadata = fileJcrContent.getChild(
                                                FormsLibraryConstants.METADATA);
                                        if (fileMetadata != null) {
                                            final ValueMap fileMetadataMap = fileMetadata.adaptTo(ValueMap.class);
                                            final Map<String, Object> fileWrapper = Maps.newHashMap();
                                            fileWrapper.put(FormsLibraryConstants.FILE_CATEGORY_TITLE,
                                                    categoryMap.get(FormsLibraryConstants.JCR_TITLE));
                                            fileWrapper.put(FormsLibraryConstants.FILE_URL, file.adaptTo(
                                                    Asset.class).getPath());
                                            fileWrapper.put(FormsLibraryConstants.EFORM_URL, Constants.BLANK);
                                            fileWrapper.put(FormsLibraryConstants.FILE_TITLE, fileMetadataMap.get(
                                                    MetLifeConstants.DC_TITLE));
                                            fileWrapper.put(FormsLibraryConstants.FILE_DESCRIPTION, fileMetadataMap.get(
                                                    MetLifeConstants.DC_DESCRIPTION));
                                            fileWrapper.put(FormsLibraryConstants.FILE_TYPE,
                                                    FilenameUtils.getExtension(file.getName()).toUpperCase());
                                            fileWrapper.put(FormsLibraryConstants.FILE_SIZE, fileMetadataMap.get(
                                                    MetLifeConstants.DAM_SIZE
                                            ));
                                            fileWrapper.put(FormsLibraryConstants.EFORM_SIZE, Constants.BLANK);
                                            categoryFiles.add(fileWrapper);
                                        } else {
                                            logger.error("fileMetadata is null");
                                        }
                                    } else {
                                        logger.error("fileJcrContent is null");
                                    }
                                }
                            }
                            customResponseData.put(DOCS, categoryFiles);
                            customResponseData.put(NUM_FOUND, categoryFiles.size());
                            break;
                        } else {
                            logger.error("categoryChildJcrContent is null");
                        }
                    }
                }
            }
        }
        customResponse.put(RESPONSE, customResponseData);

        //set application/json as the response content type
        response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

        //set UTF-8 as the character encoding
        response.setCharacterEncoding(MetLifeConstants.UTF8_CHARSET);

        response.getWriter().write(new Gson().toJson(customResponse));
    }
}
