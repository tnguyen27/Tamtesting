package com.metlife.commons.servlet;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.metlife.commons.filters.HideNavigationXMLFilter;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;

import com.xumak.base.templatingsupport.TemplatingSupportFilter;

/**.
 * Sitemap Servlet
 * Outputs a simple sitemap, using a base page and filtering out the pages that have a 'hideNavigation' property'
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2014/11/07 | Pablo Alecio  | Initial Creation
 * 1.1     | 27/02/2015 | Lesly Quiñonez| Adapt to LATAM
 * 1.2     | 16/08/2016 | Roger Jimenez | Adapt to MetLife
 * 1.3     | 30/08/2016 | Javier Garcia | Changed use of HideNavigationFilter to HideNavigationXMLFilter
 * --------------------------------------------------------------------------------------
 *
 */

@Component
@Service
@Properties({
        @Property(name = "service.description", value = "MetLife Sitemap"),
        @Property(name = "sling.servlet.extensions", value = "xml"),
        @Property(name = "sling.servlet.resourceTypes", value = {SitemapServlet.SITEMAP_RESOURCE_TYPE})
        })
public class SitemapServlet extends SlingSafeMethodsServlet {
    /**.
     * .
     */
    public static final String SITEMAP_RESOURCE_TYPE = "metlifeCommons/components/section/configurations/sitemap";
    /** . */
    private final Logger logger = LoggerFactory.getLogger(SitemapServlet.class);
    /** . */
    public static final String URLSET = "urlset";
    /** . */
    public static final String LOC = "loc";
    /** . */
    public static final String URL = "url";
    /** . */
    public static final String CHANGE_FREQ = "changefreq";
    /** . */
    public static final String PRIORITY = "priority";
    /** . */
    public static final String SITEMAP_NS = "http://www.sitemaps.org/schemas/sitemap/0.9";
    /** . */
    public static final String XSI = "xsi";
    /** . */
    public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";
    /** . */
    public static final String SCHEMA_LOCATION = "schemaLocation";
    /** . */
    public static final String GOOGLE_SCHEMA = "http://www.google.com/schemas/sitemap/0.84"
            + " http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd";
    /** . */
    public static final String APPLICATION_XML = "application/xml";
    /** . */
    public static final String CQ_TEMPLATE = "cq:template";
    /** . */
    private static final String HOME_TEMPLATE = "/apps/MetlifeApp/templates/home";
    /** . */
    private static final String PRODUCT_TEMPLATE = "/apps/MetlifeApp/templates/product";
    /** . */
    private static final String MONTHLY_VALUE = "monthly";
    /** . */
    private static final String HOME_PRIORITY_VALUE = "1";
    /** . */
    private static final String PRODUCT_PRIORITY_VALUE = "0.50";
    /** . */
    private static final String REST_TEMPLATES_VALUE = "0.25";

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("> Sitemap");
        final TemplateContentModel contentModel =
                (TemplateContentModel) request.getAttribute(TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);
        final String basePath = contentModel.getAsString(Constants.REFERENCED_PATH_CONTEXT_PN);
        final Externalizer externalizer = request.getResourceResolver().adaptTo(Externalizer.class);

        if (!basePath.isEmpty()) {
            final ResourceResolver resourceResolver = request.getResourceResolver();
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Page basePage = pageManager.getPage(basePath);  //get the base page

            final Namespace namespace = Namespace.getNamespace(SITEMAP_NS);

            final Element urlSet = new Element(URLSET, namespace);
            final Namespace namespaceXSI = Namespace.getNamespace(XSI, XML_SCHEMA);
            urlSet.addNamespaceDeclaration(namespaceXSI);
            urlSet.setAttribute(SCHEMA_LOCATION, GOOGLE_SCHEMA, namespaceXSI);

            final Document document = new Document(urlSet); //create the XML document

            if (null != basePage) {
                //filter out the pages that have a hideNavigation option set
                //true to traverse all the tree, not just the children
                final Iterator<Page> pageIterator = basePage.listChildren(new HideNavigationXMLFilter(), true);

                while (pageIterator.hasNext()) {
                    final Page childPage = pageIterator.next();
                    final Element urlElement = new Element(URL, namespace);

                    //externalize the url
                    //Getting path as external link format.
                    final String url = externalizer.absoluteLink(request,
                            request.getScheme(), childPage.getPath());

                    urlElement.addContent(new Element(LOC, namespace).setText(url + Constants.SLASH));

                    final ValueMap pageProperties = childPage.getProperties();
                    if (null != pageProperties) {
                        if (pageProperties.containsKey(CHANGE_FREQ)) {
                            final String changeFreq = (String) pageProperties.get(CHANGE_FREQ);
                            urlElement.addContent(new Element(CHANGE_FREQ, namespace).setText(changeFreq));
                        } else {
                            urlElement.addContent(new Element(CHANGE_FREQ, namespace).setText(MONTHLY_VALUE));
                        }
                        if (pageProperties.containsKey(PRIORITY)) {
                            final String priority = (String) pageProperties.get(PRIORITY);
                            urlElement.addContent(new Element(PRIORITY, namespace).setText(priority));
                        } else {
                            final String currentTemplate = childPage.getProperties().get(CQ_TEMPLATE, Constants.BLANK);
                            if (HOME_TEMPLATE.equals(currentTemplate)) {
                                urlElement.addContent(new Element(PRIORITY, namespace).setText(HOME_PRIORITY_VALUE));
                            } else if (PRODUCT_TEMPLATE.equals(currentTemplate)) {
                                urlElement.addContent(new Element(PRIORITY, namespace).setText(PRODUCT_PRIORITY_VALUE));
                            } else {
                                urlElement.addContent(new Element(PRIORITY, namespace).setText(REST_TEMPLATES_VALUE));
                            }
                        }
                    }
                    urlSet.addContent(urlElement);
                }
            } else {
                logger.error("The page with path {0} is null", basePath);
            }

            response.setContentType(APPLICATION_XML);
            document.setRootElement(urlSet);
            new XMLOutputter().output(document, response.getWriter());

        } else {
            logger.error("The base path is empty.");
            new XMLOutputter().output(new Document(), response.getWriter());
        }

    }

}

