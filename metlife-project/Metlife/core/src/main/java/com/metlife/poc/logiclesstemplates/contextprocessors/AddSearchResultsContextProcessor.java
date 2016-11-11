package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.metlife.poc.util.MetLifeConstants;
import com.xumak.base.Constants;

/**
 * AddSearchResultsContextProcessor
 * Uses the OOTB search to query the site for content.
 * Adds the search results to the content model.
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
public class AddSearchResultsContextProcessor extends
        AbstractResourceTypeCheckContextProcessor<ContentModel> {

    /**
     * RESOURCE_TYPE.
     */
    public static final String RESOURCE_TYPE =
            "MetlifeApp/components/section/search-results";
    /**
     * DATE_FORMAT.
     */
    private static final String DATE_FORMAT = "YYYY-MM-DD'T'HH:mm:ss";

    @Override
    public final String requiredResourceType() {
        return RESOURCE_TYPE;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {
        final String rootPath = contentModel.getAsString(Constants.CONTENT
                + Constants.DOT
                + MetLifeConstants.ROOT_PATH_KEY);
        final String query = request.getParameter(Constants.QUERY_PN);
        final long offset = offset(request);
        final long limit = limit(request);
        if (StringUtils.isNotBlank(rootPath) && StringUtils.isNotBlank(query)) {
            final SimpleSearch search = request.getResource().adaptTo(
                    SimpleSearch.class);
            search.setSearchIn(rootPath);
            search.setQuery(query);
            search.setStart(offset);
            search.setHitsPerPage(limit);
            final SearchResult result = search.getResult();
            if (null != result) {
                final List<Map<String, String>> docs = Lists.newArrayList();
                for (Hit hit : result.getHits()) {
                    final Map<String, String> map = Maps.newHashMap();
                    map.put(MetLifeConstants.ID_KEY, hit.getPath()
                            + MetLifeConstants.HTML_EXTENSION);
                    map.put(MetLifeConstants.URL_KEY, hit.getPath()
                            + MetLifeConstants.HTML_EXTENSION);
                    map.put(MetLifeConstants.TITLE_KEY, hit.getTitle());
                    map.put(MetLifeConstants.TYPE_KEY,
                            MetLifeConstants.TEXT_HTML);

                    //date
                    final Calendar createdDate = (Calendar) hit.getProperties().
                            get(Constants.JCR_CREATED);
                    final SimpleDateFormat sdf = new SimpleDateFormat(
                            DATE_FORMAT);
                    map.put(MetLifeConstants.DATE_KEY, sdf.format(
                            createdDate.getTime()));
                    map.put(MetLifeConstants.TIMESTAMP_KEY, sdf.format(
                            createdDate.getTime()));

                    final Calendar lastModifiedDate = (Calendar) hit.
                            getProperties().get(MetLifeConstants.LAST_MODIFIED);
                    map.put(MetLifeConstants.LAST_MODIFIED_KEY,
                            sdf.format(lastModifiedDate
                            .getTime()));

                    //content
                    final String excerpt = hit.getExcerpt();
                    map.put(MetLifeConstants.CONTENT_KEY, excerpt);
                    map.put(MetLifeConstants.CONTENT_LENGTH_KEY,
                            String.valueOf(excerpt.length()
                    ));


                    docs.add(map);
                }

                final Map<String, Object> response = Maps.newHashMap();
                response.put(MetLifeConstants.NUM_FOUND_KEY,
                        result.getTotalMatches());
                response.put(MetLifeConstants.START_KEY, offset);
                response.put(MetLifeConstants.DOCS_KEY, docs);
                contentModel.set(Constants.CONTENT + Constants.DOT
                        + MetLifeConstants.RESULTS, response);
            }

        }
    }

    /**
     * Returns the limit.
     * @param request The SlingHttpServletRequest.
     * @return Limit as a long.
     */
    private long limit(final SlingHttpServletRequest request) {
        final long thousand = 1000;
        long result = thousand;
        final String limitParam = request.getParameter(
                MetLifeConstants.LIMIT_KEY);
        /*
        return StringUtils.isNotBlank(limitParam) ? NumberUtils.toLong(
                limitParam) : oneThousand;
                */
        if (StringUtils.isNotBlank(limitParam)) {
            result = NumberUtils.toLong(limitParam);
        }
        return result;
    }

    /**
     * Returns the offset.
     * @param request The SlingHttpServletRequest.
     * @return Limit as a long.
     */
    private long offset(final SlingHttpServletRequest request) {
        long result = 0;
        final String startParam = request.getParameter(
                MetLifeConstants.START_KEY);
        /*
        return StringUtils.isNotBlank(startParam) ? NumberUtils.toLong(
                startParam) : 0;
                */
        if (StringUtils.isNotBlank(startParam)) {
            result = NumberUtils.toLong(startParam);
        }
        return result;
    }
}
