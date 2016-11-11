package com.metlife.global.logiclesstemplates.processors;

import com.google.common.collect.Sets;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * GetYearFromCurrentServerContextProcessor.
 *
 *Get Year from Server to display in
 *footer microsite.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 09/06/2016 | Sergio Torres   | Initial Creation
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class GetYearFromCurrentServerContextProcessor
        extends AbstractResourceTypeCheckContextProcessor<TemplateContentModel> {

    /**
     * HTML_BASE_RESOURCE_TYPE.
     */
    public static final String HTML_BASE_RESOURCE_TYPE =
            "metlifeMicrosites/components/page/htmlbase";

    /**
     * CURRENT YEAR.
     */
    public static final String CURRENT_YEAR = "currentYear";


    @Override
    public final boolean mustExist() {
        return false;
    }

    @Override
    public final int priority() {
        return Constants.MEDIUM_PRIORITY;
        //must be executed after any other context
        // processor
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest,
            final TemplateContentModel templateContentModel) throws Exception {

        final Map<String, Object> contentObject = (Map<String, Object>)
                templateContentModel.get(Constants.RESOURCE_CONTENT_KEY);

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final String currentYear = Integer.toString(year);
        contentObject.put(CURRENT_YEAR, currentYear);
    }

    /**
     * Get required property names.
     * @return Set of strings.
     */
    @Override
    public final Set<String> requiredResourceTypes() {
        return Sets.newHashSet(HTML_BASE_RESOURCE_TYPE);
    }
}

