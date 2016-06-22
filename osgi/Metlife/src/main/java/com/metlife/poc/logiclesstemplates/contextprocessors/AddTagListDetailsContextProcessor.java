package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Base64Encoding;
import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**.
 * AddTagListDetailsContextProcessor
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
public class AddTagListDetailsContextProcessor extends
        AbstractConfigurationContextProcessor<ContentModel> {

    /**
     * TAG_LIST_PROPERTY_NAME.
     */
    public static final String TAG_LIST_PROPERTY_NAME = "tagListPropertyName";

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(TAG_LIST_PROPERTY_NAME);
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {

        final String tagListPropertyName = contentModel.getAsString(
                Constants.CONFIG_PROPERTIES_KEY + Constants.DOT
                        + TAG_LIST_PROPERTY_NAME);
        if (contentModel.has(tagListPropertyName)) {
            final TagManager tagManager = request.getResourceResolver().adaptTo(
                    TagManager.class);
            final List<String> tagList = contentModel.getAs(tagListPropertyName,
                    List.class);
            final List<Map<String, Object>> tagListDetails =
                    Lists.newArrayList();
            for (String tagId : tagList) {
                final Tag tag = tagManager.resolve(tagId);
                if (tag != null) {
                    final Map<String, Object> map = Maps.newHashMap();
                    putPropertyInMap(map, MetLifeConstants.DESCRIPTION_KEY,
                            tag.getDescription());
                    putPropertyInMap(map, MetLifeConstants.TITLE_KEY,
                            tag.getTitle());
                    putPropertyInMap(map, Constants.ID, tagId);
                    putPropertyInMap(map, MetLifeConstants.NAMESPACE_KEY,
                            tag.getNamespace()
                            .getTagID());
                    putPropertyInMap(map, Constants.PATH, tag.getPath());
                    putPropertyInMap(map, MetLifeConstants.ENCODED_ID,
                            Base64Encoding.encode(
                            tagId));
                    tagListDetails.add(map);
                }
            }
            contentModel.set(MetLifeConstants.LIST + Constants.DOT
                    + Constants.TAGS, tagListDetails);
        }

    }

    /**
     * Put property map.
     * @param map Map<String, Object>.
     * @param key String.
     * @param value String.
     */
    private void putPropertyInMap(final Map<String, Object> map,
                                  final String key,
                                  final String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }
}
