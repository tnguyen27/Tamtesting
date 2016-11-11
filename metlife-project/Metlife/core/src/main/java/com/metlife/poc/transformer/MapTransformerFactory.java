package com.metlife.poc.transformer;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
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
 * MapTransformerFactory.
 * Transformer used for map all the attributes of a specific element.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 05/18/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Service(value = TransformerFactory.class)
@Component(label = "MetLife - Map Rewriter",
        description = "Service to map attributes of specific elements.",
        immediate = true, metatype = true)
@Properties({
        @Property(value = "xumak-map-rewriter", propertyPrivate = true, name = "pipeline.type"),
        @Property(boolValue = false, name = "activateRewriter", label = "Activate Rewrite",
                description = "When is checked activate the MetLife rewriter"),
        @Property(value = {"img:src"}, name = "rewriter.element", cardinality = Integer.MAX_VALUE,
                description = "List of element:attribute that should be mapped.")
})
public class MapTransformerFactory implements TransformerFactory {
    private final Logger logger = LoggerFactory.getLogger(
            MapTransformerFactory.class);
    private boolean activeRewrite;
    private String[] elements;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     *
     * @param properties Configurations
     */
    @Activate
    public void activate(final Map<String, String> properties) {
        logger.info("Transformer Activated...");
        this.activeRewrite = PropertiesUtil.toBoolean(properties.get("activateRewriter"), false);
        this.elements = PropertiesUtil.toStringArray(properties.get("rewriter.element"));
    }
    @Override
    public Transformer createTransformer() {
        return new MapTransformer(activeRewrite, resourceResolverFactory, Arrays.asList(elements));
    }
}
