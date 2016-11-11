package com.metlife.poc.transformer;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * LinkTransformerFactory
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
@Service(value = TransformerFactory.class)
@Component(label = "MetLife - Link Rewriter",
        description = "Service to enable html replace with / character",
        immediate = true, metatype = true)
@Properties({
        @Property(value = "xumak-link-rewriter", propertyPrivate = true, name = "pipeline.type"),
        @Property(boolValue = false, name = "activateRewriter", label = "Activate Rewrite",
        description = "When is checked activate the MetLife rewriter"),
        @Property(value = {"href"}, name = "rewriter.attributes", cardinality = Integer.MAX_VALUE)
        })

public class LinkTransformerFactory implements TransformerFactory {
    private final Logger logger = LoggerFactory.getLogger(
            LinkTransformerFactory.class);
    private  boolean activateRewrite;
    private String[] attributes;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     *
     * @param properties Custom properties
     */
    @Activate
    public void activate(final Map<String, String> properties) {
        logger.info("Transformer Activated...");
        this.activateRewrite = PropertiesUtil.toBoolean(properties.get("activateRewriter"), false);
        this.attributes = PropertiesUtil.toStringArray(properties.get("rewriter.attributes"));

    }

    /**
     *
     * @return The Link transformer
     */
    public final Transformer createTransformer() {
        return new LinkTransformer(activateRewrite, resourceResolverFactory, Arrays.asList(attributes));
    }
}
