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
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * MapTransformer.
 * Transformer used for map all the attributes of a specific element.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 05/18/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public class MapTransformer extends AbstractSAXPipe implements Transformer {
    private final Logger logger = LoggerFactory.getLogger(
            MapTransformer.class);
    private boolean activateRewrite;
    private final ResourceResolverFactory resourceResolverFactory;
    private List elements;

    /**
     *
     * @param activateRewrite true if the transformer is active
     * @param resourceResolverFactory Resource Resolver Factory
     * @param elements list of elements that should to be mapped
     */
    public MapTransformer(final boolean activateRewrite, final ResourceResolverFactory resourceResolverFactory,
                           final List elements) {
        this.activateRewrite = activateRewrite;
        this.resourceResolverFactory = resourceResolverFactory;
        this.elements = elements;
    }

    @Override
    public void init(ProcessingContext processingContext,
                     ProcessingComponentConfiguration processingComponentConfiguration) throws IOException {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void startElement(final String uri, final String name, final String raw,
                             final Attributes attrs) throws SAXException {
        final AttributesImpl attributes = new AttributesImpl(attrs);
        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            if (activateRewrite) {
                logger.debug("Begin transformer for element " + name);
                final Iterator<String> elementIterator = elements.iterator();
                while (elementIterator.hasNext()) {
                    final String[] element = elementIterator.next().split(":");
                    if (element.length == 2 && element[0].equalsIgnoreCase(name)) {
                        final String attrVal = attributes.getValue(element[1]);
                        if (StringUtils.isNotEmpty(attrVal)) {
                            attributes.setValue(attributes.getIndex(element[1]),
                                    resourceResolver.map(attrVal));
                        }
                    }
                }
            }
        } catch (LoginException e) {
            e.printStackTrace();
        }
        super.startElement(uri, name, raw, attributes);
    }
}
