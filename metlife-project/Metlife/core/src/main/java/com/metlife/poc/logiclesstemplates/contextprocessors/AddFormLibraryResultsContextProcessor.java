package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.commons.RangeIterator;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.metlife.poc.util.Base64Encoding;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.SelectorUtils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * AddFormLibraryResultsContextProcessor.
 *
 * *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2016/02/04 | Palecio       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class AddFormLibraryResultsContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {

    /**
     * Logger.
     */
    private Logger logger = LoggerFactory.getLogger(
            AddFormLibraryResultsContextProcessor.class);

    /**
     * ONETHOUSAND.
     */
    private static final int ONETHOUSAND = 1000;

    /**
     * RESOURCE_TYPE.
     */
    public static final String RESOURCE_TYPE =
            "MetlifeApp/components/section/form-library";
    /**
     * ASSET_TYPE_MAP.
     */
    private static final ImmutableMap<String, String> ASSET_TYPE_MAP =
            new ImmutableMap.Builder<String, String>()
                    .put("application/pdf", "PDF")
                    .put("application/vnd.openxmlformats-officedocument"
                            + ".spreadsheetml.sheet", "XLSX")
                    .build();

    @Override
    public final String requiredResourceType() {
        return RESOURCE_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        Map<String, Object> doc = null;
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors.length == 2 && MetLifeConstants.RESULTS
                .equals(selectors[0])) {
            final Map<String, String> params = SelectorUtils.parseParams(
                    selectors);
            final String encodedProduct = params.get(MetLifeConstants.PRODUCT);
            final String product = Base64Encoding.decode(encodedProduct);

            logger.info("product: {}", product);

            if (StringUtils.isNotBlank(product)) {
                final TagManager tagManager =
                        request.getResourceResolver().adaptTo(TagManager.class);
                final RangeIterator<Resource> taggedResources = tagManager.find(
                        product);
                if (taggedResources != null) {
                    final List<Map<String, Object>> docs = Lists.newArrayList();
                    while (taggedResources.hasNext()) {
                        doc = getDocument(taggedResources,
                                tagManager, product, ONETHOUSAND);
                        docs.add(doc);
                    }

                    final Map<String, Object> contentObject =
                            (Map<String, Object>)
                            contentModel.get(Constants.RESOURCE_CONTENT_KEY);
                    final List<Map<String, Object>> metas =
                            Lists.newArrayList();
                    doc = Maps.newHashMap();

                    if (contentObject.containsKey(MetLifeConstants
                            .CATEGORY_TITLE)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.CATEGORY_TITLE).toString())) {
                        doc.put(MetLifeConstants.CATEGORY_TITLE,
                                contentObject.get(
                                MetLifeConstants.CATEGORY_TITLE).toString());
                    } else {
                        doc.put(MetLifeConstants.CATEGORY_TITLE, "");
                    }

                    if (contentObject.containsKey(MetLifeConstants
                            .CATEGORY_DESCRIPTION)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.CATEGORY_DESCRIPTION)
                            .toString())) {
                        doc.put(MetLifeConstants.CATEGORY_DESCRIPTION,
                                contentObject.get(
                                MetLifeConstants.CATEGORY_DESCRIPTION)
                                        .toString());
                    } else {
                        doc.put(MetLifeConstants.CATEGORY_DESCRIPTION, "");
                    }

                    if (contentObject.containsKey(MetLifeConstants
                            .DOWNLOAD_TEXT)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.DOWNLOAD_TEXT).toString())) {
                        doc.put(MetLifeConstants.DOWNLOAD_TEXT,
                                contentObject.get(
                                MetLifeConstants.DOWNLOAD_TEXT).toString());
                    } else {
                        doc.put(MetLifeConstants.DOWNLOAD_TEXT, "");
                    }

                    if (contentObject.containsKey(MetLifeConstants
                            .OTHER_FORMS_TITLE)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.OTHER_FORMS_TITLE).toString())) {
                        doc.put(MetLifeConstants.OTHER_FORMS_TITLE,
                                contentObject.get(
                                MetLifeConstants.OTHER_FORMS_TITLE).toString());
                    } else {
                        doc.put(MetLifeConstants.OTHER_FORMS_TITLE, "");
                    }

                    if (contentObject.containsKey(MetLifeConstants
                            .OTHER_FORMS_DESCRIPTION)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.OTHER_FORMS_DESCRIPTION)
                            .toString())) {
                        doc.put(MetLifeConstants.OTHER_FORMS_DESCRIPTION,
                                contentObject.get(
                                MetLifeConstants.OTHER_FORMS_DESCRIPTION)
                                        .toString());
                    } else {
                        doc.put(MetLifeConstants.OTHER_FORMS_DESCRIPTION, "");
                    }

                    if (contentObject.containsKey(MetLifeConstants
                            .OTHER_FORMS_DETAILS)
                            && StringUtils.isNotBlank(contentObject.get(
                            MetLifeConstants.OTHER_FORMS_DETAILS).toString())) {
                        doc.put(MetLifeConstants.OTHER_FORMS_DETAILS,
                                contentObject.get(
                                MetLifeConstants.OTHER_FORMS_DETAILS)
                                        .toString());
                    } else {
                        doc.put(MetLifeConstants.OTHER_FORMS_DETAILS, "");
                    }

                    metas.add(doc);

                    final Map<String, Object> response = Maps.newHashMap();
                    response.put(MetLifeConstants.METADATA, metas);
                    response.put(MetLifeConstants.DOCS, docs);
                    response.put(MetLifeConstants.START_KEY, 0);
                    response.put(MetLifeConstants.NUM_FOUND_KEY, docs.size());
                    contentModel.set(Constants.CONTENT + Constants.DOT
                            + MetLifeConstants.RESULTS, response);
                }
            }
        }
    }

    /**
     * Get a document by the tag manager.
     * @param taggedResources tagged resource
     * @param tagManager Tag manager.
     * @param product product.
     * @param oneThousand thousand value
     * @return document added
     */
    private Map<String, Object> getDocument(final RangeIterator<Resource>
                                                     taggedResources,
    final TagManager tagManager, final String product, final  int oneThousand) {

        Map<String, Object> doc = null;
        final Resource taggedResource = taggedResources.next();
        final Resource contentResource =
                taggedResource.getParent();
        if (contentResource != null) {
            final Resource assetResource = contentResource
                    .getParent();
            if (assetResource != null) {
                doc = Maps.newHashMap();
                final Asset asset = assetResource.adaptTo(Asset
                        .class);
                if (asset != null) {
                    doc.put(MetLifeConstants.ID_KEY,
                            asset.getPath());
                    doc.put(MetLifeConstants.FRM_LIB_FILE_URL,
                            asset.getPath());
                    doc.put(MetLifeConstants.CUSTOM_URL_KEY,
                            asset.getPath());

                    String title = asset.getMetadataValue(
                            MetLifeConstants.DC_TITLE);
                    if (!StringUtils.isNotBlank(title)) {
                        title = asset.getName();
                    }
                                    /*
                                    title = StringUtils.isNotBlank(title)
                                            ? title : asset.getName();
                                            */
                    doc.put(MetLifeConstants.FRM_LIB_FILE_TITLE,
                            title);
                    final String description = asset
                            .getMetadataValue(MetLifeConstants
                                    .DC_DESCRIPTION);
                    if (StringUtils.isNotBlank(description)) {
                        doc.put(MetLifeConstants
                                        .FRM_LIB_FILE_DESCRIPTION,
                                description);
                    }
                    final Tag productTag = tagManager.resolve(
                            product);
                    final String tagTitle =
                            productTag.getTitle();
                    doc.put(MetLifeConstants.CATEGORY_NAME_KEY,
                            tagTitle);
                    doc.put(MetLifeConstants.CATEGORY_TITLE_KEY,
                            tagTitle);
                    doc.put(MetLifeConstants
                                    .FRM_LIB_FILE_CATEGORY_TITLE,
                            tagTitle);

                    final String type = asset.getMetadataValue(
                            MetLifeConstants.DC_FORMAT);
                    if (StringUtils.isNotBlank(type)) {
                        doc.put(MetLifeConstants
                                        .FRM_LIB_FILE_TYPE,
                                ASSET_TYPE_MAP.get(type));
                    }
                    final long size = (long) asset.getMetadata(
                            MetLifeConstants.DAM_SIZE);
                    doc.put(MetLifeConstants.FRM_LIB_FILE_SIZE,
                            size / oneThousand);

                }
            }
        }

        return doc;



    }
}
