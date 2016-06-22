package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.poc.util.ContainerType;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AddTileContainerConfigurationContextProcessor.
 */
@Component
@Service
public class AddTileContainerConfigurationContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * TILE_CONTAINER_RESOURCE_TYPE.
     */
    public static final String TILE_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/tile-container";
    /**
     * SUBCATEGORY_TILE_CONTAINER_RESOURCE_TYPE.
     */
    public static final
    String SUBCATEGORY_TILE_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/subcategory-tile-container";
    /**
     * MEDIUM_PRODUCT_CONTAINER_RESOURCE_TYPE.
     */
    public static final
    String MEDIUM_PRODUCT_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/medium-product-container";
    /**
     * CONTEXTUAL_LINKS_CONTAINER_RESOURCE_TYPE.
     */
    public static final
    String CONTEXTUAL_LINKS_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/contextual-links-container";
    /**
     * SMALL_PRODUCT_CONTAINER_RESOURCE_TYPE.
     */
    public static final
    String SMALL_PRODUCT_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/small-product-container";
    /**
     * LIFE_STAGES_CONTAINER_RESOURCE_TYPE.
     */
    public static final
    String LIFE_STAGES_CONTAINER_RESOURCE_TYPE =
            "MetlifeApp/components/section/life-stages-container";
    /**
     * HOME_SUBNAVIGATION_RESOURCE_TYPE.
     */
    public static final
    String HOME_SUBNAVIGATION_RESOURCE_TYPE =
            "MetlifeApp/components/section/home-subnav";
    /**
     * SITEMAP_RESOURCE_TYPE.
     */
    public static final
    String SITEMAP_RESOURCE_TYPE =
            "MetlifeApp/components/section/sitemap-category";
    /**
     * SITEMAP_ROW_RESOURCE_TYPE.
     */
    public static final
    String SITEMAP_ROW_RESOURCE_TYPE =
            "MetlifeApp/components/section/sitemap-category-row";

    /**
     * TILE_LIST_CONTEXT_KEY.
     */
    public static final String TILE_LIST_CONTEXT_KEY = "list.tiles";
    /**
     * BANNER_KEY.
     */
    public static final String BANNER_KEY = "content.banner";
    /**
     * ONLY_BANNER_KEY.
     */
    public static final String ONLY_BANNER_KEY = "content.onlyBanner";
    /**
     * TYPE.
     */
    public static final String TYPE = "type";
    /**
     * ROW_TYPE.
     */
    public static final String ROW_TYPE = "rowType";
    /**
     * COLUMN_TYPE.
     */
    public static final String COLUMN_TYPE = "columnType";
    /**
     * ONE_ITEM.
     */
    public static final String ONE_ITEM = "1-item";
    /**
     * TWO_ITEMS.
     */
    public static final String TWO_ITEMS = "2-items";
    /**
     * THREE_ITEMS.
     */
    public static final String THREE_ITEMS = "3-items";
    /**
     * ORDER.
     */
    public static final String ORDER = "order";
    /**
     * BANNER_FIRST.
     */
    public static final String BANNER_FIRST = "banner-first";
    /**
     * TILE_FIRST.
     */
    public static final String TILE_FIRST = "tile-first";
    /**
     * BANNER.
     */
    public static final String BANNER = "banner";
    /**
     * ONLY_BANNER_TILE.
     */
    public static final String ONLY_BANNER_TILE = "banner-tile";
    /**
     * FIRST_TILE.
     */
    public static final String FIRST_TILE = "first-tile";
    /**
     * SECOND_TILE.
     */
    public static final String SECOND_TILE = "second-tile";
    /**
     * THIRD_TILE.
     */
    public static final String THIRD_TILE = "third-tile";
    /**
     * FIRST_TILE_TYPE.
     */
    public static final String FIRST_TILE_TYPE = "tile1Type";
    /**
     * SECOND_TILE_TYPE.
     */
    public static final String SECOND_TILE_TYPE = "tile2Type";
    /**
     * THIRD_TILE_TYPE.
     */
    public static final String THIRD_TILE_TYPE = "tile3Type";
    /**
     * PROMOTION.
     */
    public static final String PROMOTION = "promotion";
    /**
     * ROW_LIST_CONTEXT_KEY.
     */
    public static final String ROW_LIST_CONTEXT_KEY = "list.rows";
    /**
     * COLUMN_LIST_CONTEXT_KEY.
     */
    public static final String COLUMN_LIST_CONTEXT_KEY = "list.columns";


    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(TILE_CONTAINER_RESOURCE_TYPE,
                SUBCATEGORY_TILE_CONTAINER_RESOURCE_TYPE,
                MEDIUM_PRODUCT_CONTAINER_RESOURCE_TYPE,
                CONTEXTUAL_LINKS_CONTAINER_RESOURCE_TYPE,
                MEDIUM_PRODUCT_CONTAINER_RESOURCE_TYPE,
                LIFE_STAGES_CONTAINER_RESOURCE_TYPE,
                SMALL_PRODUCT_CONTAINER_RESOURCE_TYPE,
                HOME_SUBNAVIGATION_RESOURCE_TYPE, SITEMAP_RESOURCE_TYPE,
                SITEMAP_ROW_RESOURCE_TYPE);
    }

    /**
     * Set tile container.
     * @param contentModel TemplateContentModel.
     */
    private void setTileContainer(final TemplateContentModel contentModel) {
        final List<Map<String, Object>> items = Lists.newArrayList();
        final String type = contentModel.getAsString(Constants.CONTENT
                + Constants.DOT + TYPE);
        if (TWO_ITEMS.equals(type)) {
            final String order = contentModel.getAsString(Constants.CONTENT
                    + Constants.DOT
                    + ORDER);
            String first = Constants.BLANK;
            String second = Constants.BLANK;
            boolean firstTileType = false;
            boolean secondTileType = false;

            if (BANNER_FIRST.equals(order)) {
                first = BANNER;
                second = FIRST_TILE;
                firstTileType = true;

            } else if (TILE_FIRST.equals(order)) {
                first = FIRST_TILE;
                second = BANNER;
                secondTileType = true;
            }

            final Map<String, Object> map1 = Maps.newHashMap();
            map1.put(Constants.TITLE, first);
            map1.put(TYPE, firstTileType);
            items.add(map1);

            final Map<String, Object> map2 = Maps.newHashMap();
            map2.put(Constants.TITLE, second);
            map2.put(TYPE, secondTileType);
            items.add(map2);

            contentModel.set(BANNER_KEY, Constants.TRUE);

        } else if (THREE_ITEMS.equals(type)) {
            final String firstTileType = contentModel.getAsString(
                    Constants.CONTENT + Constants.DOT + FIRST_TILE_TYPE);
            final String secondTileType = contentModel.getAsString(
                    Constants.CONTENT + Constants.DOT + SECOND_TILE_TYPE);
            final String thirdTileType = contentModel.getAsString(
                    Constants.CONTENT + Constants.DOT + THIRD_TILE_TYPE);

            final Map<String, Object> map1 = Maps.newHashMap();
            final Map<String, Object> map2 = Maps.newHashMap();
            final Map<String, Object> map3 = Maps.newHashMap();

            map1.put(Constants.TITLE, FIRST_TILE);
            map1.put(TYPE, PROMOTION.equals(firstTileType));

            map2.put(Constants.TITLE, SECOND_TILE);
            map2.put(TYPE, PROMOTION.equals(secondTileType));

            map3.put(Constants.TITLE, THIRD_TILE);
            map3.put(TYPE, PROMOTION.equals(thirdTileType));

            items.add(map1);
            items.add(map2);
            items.add(map3);

        } else if (ONE_ITEM.equals(type)) {
            final Map<String, Object> map1 = Maps.newHashMap();
            map1.put(Constants.TITLE, ONLY_BANNER_TILE);
            contentModel.set(ONLY_BANNER_KEY, Constants.TRUE);
            items.add(map1);
        }

        if (!items.isEmpty()) {
            contentModel.set(TILE_LIST_CONTEXT_KEY, items);
        }
    }


    /**
     * Set sitemap container.
     * @param contentModel TemplateContentModel.
     * @param containerType ContainerType.
     * @param contextKey String.
     * @param typeParam String.
     */
    private void setSitemapContainer(final TemplateContentModel contentModel,
                                     final ContainerType containerType,
                                     final String contextKey,
                                     final String typeParam) {

        final String type = contentModel.getAsString(Constants.CONTENT
                + Constants.DOT + typeParam);
        final List<Map<String, Object>> items = Lists.newArrayList();
        if (StringUtils.isNumeric(type.trim())) {
            int size = 0;
            try {
                size = Integer.parseInt(type.trim());
            } catch (NumberFormatException e) {
                size = 0;
            }
            for (int i = 0; i < size; i++) {
                final Map<String, Object> temp = Maps.newHashMap();
                temp.put(Constants.TITLE, containerType.name().toLowerCase()
                        + "-" + i);
                items.add(temp);
            }
        }
        if (!items.isEmpty()) {
            contentModel.set(contextKey, items);
        }


    }


    /**
     * Set subcategory tile container.
     * @param contentModel TemplateContentModel.
     */
    private void setSubcategoryTileContainer(final TemplateContentModel
                                                     contentModel) {
        final String type = contentModel.getAsString(Constants.CONTENT
                + Constants.DOT + TYPE);
        final List<Map<String, Object>> items = Lists.newArrayList();
        if (StringUtils.isNumeric(type.trim())) {
            int size = 0;

            try {
                size = Integer.parseInt(type.trim());
            } catch (NumberFormatException e) {
                size = 0;
            }
            for (int i = 0; i < size; i++) {
                final Map<String, Object> temp = Maps.newHashMap();
                temp.put(Constants.TITLE, "tile-" + i);
                items.add(temp);
            }
        }
        if (!items.isEmpty()) {
            contentModel.set(TILE_LIST_CONTEXT_KEY, items);
        }
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {
        if (request.getResource().getResourceType().endsWith(
                TILE_CONTAINER_RESOURCE_TYPE)) {
            setTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                SUBCATEGORY_TILE_CONTAINER_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                MEDIUM_PRODUCT_CONTAINER_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                CONTEXTUAL_LINKS_CONTAINER_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                SMALL_PRODUCT_CONTAINER_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                LIFE_STAGES_CONTAINER_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                HOME_SUBNAVIGATION_RESOURCE_TYPE)) {
            setSubcategoryTileContainer(contentModel);
        } else if (request.getResource().getResourceType().endsWith(
                SITEMAP_RESOURCE_TYPE)) {
            setSitemapContainer(contentModel, ContainerType.ROW,
                    ROW_LIST_CONTEXT_KEY, ROW_TYPE);
        } else if (request.getResource().getResourceType().endsWith(
                SITEMAP_ROW_RESOURCE_TYPE)) {
            setSitemapContainer(contentModel, ContainerType.COLUMN,
                    COLUMN_LIST_CONTEXT_KEY, COLUMN_TYPE);
        }

    }

    @Override
    public final boolean mustExist() {
        return false;
    }

}
