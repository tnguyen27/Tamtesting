package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.configuration.Mode;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Set;

/**
 * AddListPropertiesContextProcessor
 * This context processor returns the property as a list.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 15/12/2014 | Lesly Quiñonez  | Initial Creation
 * --------------------------------------------------------------------------------------
 */

@Component
@Service
public class AddListPropertiesContextProcessor
        extends AbstractConfigurationContextProcessor {
    private static final String XK_LIST_PROPERTIES = "xk_listProperties";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    protected XCQBConfigurationProvider configurationProvider;

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY;
    }

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_LIST_PROPERTIES);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public boolean mustExist() {
        return false;
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel)
            throws Exception {
        final XCQBConfiguration configuration = configurationProvider.getFor(request.getResource().getResourceType());
        final Collection<String> propsWithList = configuration.asStrings(XK_LIST_PROPERTIES, Mode.MERGE);

        for (final String propName : propsWithList) {
            final Collection propValue = Utils.getPropertyAsList(contentModel, propName);
            contentModel.set(propName, propValue);
        }
    }


}
