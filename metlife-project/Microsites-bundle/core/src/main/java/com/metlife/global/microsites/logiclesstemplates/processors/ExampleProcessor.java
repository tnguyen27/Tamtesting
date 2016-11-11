package com.metlife.global.microsites.logiclesstemplates.processors;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Map;

@Component
@Service
public class ExampleProcessor
        extends AbstractResourceTypeCheckContextProcessor<ContentModel> {

    @Override
    public String requiredResourceType() {
        return "metlifeMicrosites/components/section/titleexample";

    }

    @Override
    protected boolean mustExist() {
        return false;
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel)
            throws Exception {
        final Map<String, Object> contentObject =
                (Map<String, Object>) contentModel.get(Constants.RESOURCE_CONTENT_KEY);
        contentObject.put("exampleText", "This is a text from a processor :)");
    }
}
