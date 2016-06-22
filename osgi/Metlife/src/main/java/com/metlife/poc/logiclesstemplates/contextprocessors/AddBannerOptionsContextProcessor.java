package com.metlife.poc.logiclesstemplates.contextprocessors;

import com.xumak.base.Constants;
import com.xumak.base.templatingsupport
        .AbstractResourceTypeCheckContextProcessor;
import com.xumak.base.templatingsupport.TemplateContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * AddBannerOptionsContextProcessor class.
 */
@Component
@Service
public class AddBannerOptionsContextProcessor
        extends AbstractResourceTypeCheckContextProcessor
        <TemplateContentModel> {

    /**
     * BANNER_RESOURCE_TYPE.
     */
    public static final String BANNER_RESOURCE_TYPE =
            "MetlifeApp/components/section/abstract-banner";
    /**
     * SIZE_KEY.
     */
    public static final String SIZE_KEY = "content.size";
    /**
     * COLOR_KEY.
     */
    public static final String COLOR_KEY = "content.buttonColor";
    /**
     * IMAGE_POSITION_KEY.
     */
    public static final String IMAGE_POSITION_KEY = "content.imagePosition";

    /**
     * LARGE.
     */
    public static final String LARGE = "large";
    /**
     * BLUE.
     */
    public static final String BLUE = "blue";
    /**
     * LEFT.
     */
    public static final String LEFT = "left";

    @Override
    public final String requiredResourceType() {
        return BANNER_RESOURCE_TYPE;
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    TemplateContentModel contentModel)
            throws Exception {

        if (LARGE.equals(contentModel.getAsString(SIZE_KEY))) {
            contentModel.set(Constants.CONTENT + Constants.DOT + LARGE,
                    Constants.TRUE);
        }

        if (BLUE.equals(contentModel.getAsString(COLOR_KEY))) {
            contentModel.set(Constants.CONTENT + Constants.DOT + BLUE,
                    Constants.TRUE);
        }

        if (LEFT.equals(contentModel.getAsString(IMAGE_POSITION_KEY))) {
            contentModel.set(Constants.CONTENT + Constants.DOT + LEFT,
                    Constants.TRUE);
        }
    }

    @Override
    public final boolean mustExist() {
        return false;
    }

}
