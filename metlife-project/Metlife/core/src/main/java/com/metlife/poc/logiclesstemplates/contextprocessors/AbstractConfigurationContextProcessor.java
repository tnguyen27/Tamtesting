package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.
        AbstractCheckResourceExistenceContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import com.xumak.base.templatingsupport.TemplateContentModel;
import com.xumak.base.templatingsupport.TemplatingSupportFilter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * AbstractConfigurationContextProcessor
 *
 * This abstract should be used to components that will be executed when
 * have specific
 * configuration properties.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 22/04/2015 | Lesly Quiñonez  | Initial Creation
 * 1.1     | 2015/05/12 | Pablo Alecio    | Refactoring. Changed the name of
 * a couple of variables.
 * ----------------------------------------------------------------------------
 * @param <C>
 */
@Component(componentAbstract = true)
@Service
public abstract class AbstractConfigurationContextProcessor<C extends
        ContentModel>
        extends AbstractCheckResourceExistenceContextProcessor<C> {

    /**
     * This method is used to get all the configuration properties required
     * on the context processor.
     * Override this function to add the configuration properties.
     * @return Set<String>
     */
    public abstract Set<String> requiredPropertyNames();

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    public abstract Collection<String> getOptionalConfigurations();

    /**
     * hasOptionalConfiguration.
     * @param configuration optional configurations map
     * @return boolean
     */
    public final boolean hasOptionalConfiguration(final Map<String, Object>
                                                    configuration) {
        boolean isAccepted = false;
        if (getOptionalConfigurations() != null) {
            final Iterator iteratorOptionalConfs = getOptionalConfigurations()
                    .iterator();
            if (iteratorOptionalConfs.hasNext()) {
                while (iteratorOptionalConfs.hasNext()) {
                    isAccepted |= configuration.containsKey(
                            iteratorOptionalConfs.next());
                }
            } else {
                isAccepted = true;
            }
        }
        return isAccepted;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean accepts(final SlingHttpServletRequest request) {
        boolean isAccepted = false;
        try {
           final TemplateContentModel contentModel =
               (TemplateContentModel) request.getAttribute(
                 TemplatingSupportFilter.TEMPLATE_CONTENT_MODEL_ATTR_NAME);
            final Map<String, Object> configuration = contentModel.getAs(
                    Constants.CONFIG_PROPERTIES_KEY, Map.class);
            isAccepted = super.accepts(request);
            isAccepted &= hasOptionalConfiguration(configuration);
            for (final String requiredPropertyName : requiredPropertyNames()) {
                isAccepted &= configuration.containsKey(requiredPropertyName);
            }
        } catch (Exception e) {
            log.error("[ AbstractConfigurationContextProcessor : accepts ] : "
                    + "{}", e.getMessage());
        }
        return isAccepted;
    }
}
