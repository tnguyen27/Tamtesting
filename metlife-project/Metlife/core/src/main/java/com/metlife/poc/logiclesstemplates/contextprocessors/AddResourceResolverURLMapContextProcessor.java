package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.xumak.base.templatingsupport.AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Created by j.amorataya on 6/16/16.
 */
@Component
@Service
public class AddResourceResolverURLMapContextProcessor
        extends AbstractResourceTypeCheckContextProcessor<TemplateContentModel> {

    /**
     * banner resource type.
     */
    public static final String XK_IMAGE_RESOURCE_TYPE =
            "xumakbase/components/section/images/single";

    /**
     *image path.
     */
    private static final String IMAGE_TO_RESOLVE = "content.imagePath";

    @Override
    public final String requiredResourceType() {
        return XK_IMAGE_RESOURCE_TYPE;
    }

    @Override
    public void process(SlingHttpServletRequest request, TemplateContentModel contentModel) throws Exception {


        if (contentModel.has(IMAGE_TO_RESOLVE)) {
            if (contentModel.get(IMAGE_TO_RESOLVE) instanceof String) {
                final String propertyString = contentModel.getAsString(
                        IMAGE_TO_RESOLVE);

                if (!propertyString.isEmpty() && propertyString.length() > 1) {
                    final ResourceResolver resourceResolver = request.getResourceResolver();
                    contentModel.set(IMAGE_TO_RESOLVE, resourceResolver.map(propertyString));
                }


            }
        }
    }
}
