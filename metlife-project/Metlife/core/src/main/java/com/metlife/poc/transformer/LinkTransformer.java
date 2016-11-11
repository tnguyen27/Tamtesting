package com.metlife.poc.transformer;

import org.apache.cocoon.xml.sax.AbstractSAXPipe;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * LinkTransformer
 *
 * This transformer removes the html extension and replaces it by a slash.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | undefined  | Jorge Escobar   | Initial Creation
 * 1.1     | 05/17/2016 | Lesly Quinonez  | Adapt to Metlife.
 * --------------------------------------------------------------------------------------
 */

public class LinkTransformer extends AbstractSAXPipe implements Transformer {
    private final Logger logger = LoggerFactory.getLogger(
            LinkTransformer.class);
    private boolean activateRewrite;
    private final ResourceResolverFactory resourceResolverFactory;
    private List attributes;
    private static final String HTML_EXTENSION = ".html";
    private static final String SLASH = "/";

    /**
     *
     * @param activateRewrite true if the transformer is active
     * @param resourceResolver The resource resolver
     * @param attributes List of all the attributes that the value should to be transformed
     */
    public LinkTransformer(final boolean activateRewrite, final ResourceResolverFactory resourceResolver,
                           final List attributes) {
        this.activateRewrite = activateRewrite;
        this.resourceResolverFactory = resourceResolver;
        this.attributes = attributes;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void init(final ProcessingContext context, final
    ProcessingComponentConfiguration config) throws IOException {
    }

    @Override
    public void startElement(final String uri, final String name, final
    String raw, final Attributes attrs) throws SAXException {

       final AttributesImpl attributes = new AttributesImpl(attrs);
        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            if (activateRewrite) {
                logger.debug("Begin transformer for element " + name);
                final Iterator attributesIterator = this.attributes.iterator();
                while (attributesIterator.hasNext()) {
                    final String attr = (String) attributesIterator.next();
                    final String attrVal = attributes.getValue(attr);
                    if (StringUtils.isNotEmpty(attrVal) && attrVal.startsWith(SLASH)) {
                        final String originalValue = resourceResolver.map(attrVal);
                        attributes.setValue(attributes.getIndex(attr),
                                originalValue.replace(HTML_EXTENSION, SLASH));
                    }
                }
           }
        } catch (LoginException e) {
            e.printStackTrace();
        }
        super.startElement(uri, name, raw, attributes);
    }
}
