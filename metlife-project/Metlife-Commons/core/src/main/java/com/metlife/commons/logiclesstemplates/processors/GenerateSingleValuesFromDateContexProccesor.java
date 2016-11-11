package com.metlife.commons.logiclesstemplates.processors;

//import com.day.cq.wcm.api.Page;
//import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
//import com.xumak.base.util.PropertyUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;


/**.
 * GenerateSingleValuesFromDateContexProccesor
 *
 * Generate values for day month and year using
 * the xk_getValuesFromDate property.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 2016/07/21 | Roger Jimenez   | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Component
@Service
public class GenerateSingleValuesFromDateContexProccesor extends AbstractConfigurationContextProcessor {

    /**
     * XK_GETVALUEFROMDATE.
     */
    private static final String XK_GETVALUEFROMDATE =
            "xk_getValuesFromDate";
    /**
     * LIST_CONFIGURATION_PROPERTY_NAME.
     */
    public static final String LIST_CONFIGURATION_PROPERTY_NAME =
            "xk_getListOfMonthsNames";

    private static final String DATE_FORMAT = "MM/dd/yy";
    private static final String DAY_VALUE = "day";
    private static final String MONTH_VALUE = "month";
    private static final String YEAR_VALUE = "year";
    private static final String JCR_CONTENT_KEY = "jcr:content";
    private static final String LIST_KEY = "list";
    private static final String SLASH = "/";
    private static final String JCR_TITLE_KEY = "jcr:title";
    private static final String VALUE_KEY = "value";
    private static final String INVALID_MONTH = "Invalid Month";
    private static final String EMPTY_STRING = "";

    @Override
    protected final boolean mustExist() {
        return false;
    }


    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY;
    }

    @Override
    public final Set<String> requiredPropertyNames() {

        return Sets.newHashSet(XK_GETVALUEFROMDATE, LIST_CONFIGURATION_PROPERTY_NAME);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    /**
     * Logger.
     */
    //private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(
    //        GetTypeOfFileIconContextProcessor.class);


    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);

        final String keyValue = Constants.CONFIG_PROPERTIES_KEY + Constants.DOT  + XK_GETVALUEFROMDATE;

        final List<String> operationsList = (List<String>)
                Utils.getPropertyAsList(contentModel, keyValue);

        for (final String currentOperation : operationsList) {
            if (contentModel.has(currentOperation)) {
                String dateValue = null;
                dateValue = (String) contentModel.getAsString(currentOperation);
                if (!dateValue.isEmpty()) {
                    final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    final Date date = format.parse(dateValue);
                    final GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    contentObject.put(DAY_VALUE, calendar.get(Calendar.DAY_OF_MONTH));
                    String monthName = EMPTY_STRING;
                    monthName = getMonthName(slingHttpServletRequest, contentModel, calendar.get(Calendar.MONTH));
                    contentObject.put(MONTH_VALUE, monthName);
                    contentObject.put(YEAR_VALUE, calendar.get(Calendar.YEAR));
                }
            }
        }
    }

    /**
     * Looks for the pathRefs key name under config. If not found, gets the
     * default one
     * @param slingHttpServletRequest sling
     * @param contentModel content
     * @param month the request resource
     * @return monthString
     * @throws Exception exception
     */
    public String getMonthName(
            final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel, final int month)
            throws Exception {
        String monthString = INVALID_MONTH;

        final XCQBConfiguration configuration = configurationProvider.getFor(
                slingHttpServletRequest.getResource().getResourceType());
        final String configuredIconListPath = configuration.asString(LIST_CONFIGURATION_PROPERTY_NAME);
        if (!configuredIconListPath.isEmpty()) {
            final String path = contentModel.getAsString(configuredIconListPath);
            if (!path.isEmpty()) {
                final String pathfinal = path + SLASH + JCR_CONTENT_KEY + SLASH + LIST_KEY;
                final Resource resource = slingHttpServletRequest.getResourceResolver().getResource(pathfinal);
                if (null != resource) {
                    final Node node = resource.adaptTo(Node.class);
                    if (null != node) {
                        final NodeIterator nodeIterator = node.getNodes();
                        while (nodeIterator.hasNext()) {
                            final Node child = nodeIterator.nextNode();
                            if (child.hasProperty(JCR_TITLE_KEY)) {
                                final String monthValue = child.getProperty(JCR_TITLE_KEY).getString();
                                if (!monthValue.isEmpty()) {
                                    final int monthValueInt = Integer.parseInt(monthValue);
                                    if (month == monthValueInt) {
                                        if (child.hasProperty(VALUE_KEY)) {
                                            monthString = child.getProperty(VALUE_KEY).getString();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return monthString;

    }
}
