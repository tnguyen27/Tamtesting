package com.metlife.poc.util;

import com.google.common.collect.Maps;

import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by palecio on 2/8/16.
 */
public final class SelectorUtils {
    /**
     * Selector Utils.
     */
    private SelectorUtils() {
    }

    /**
     * Parses the selector string array and converts it into a map.
     *
     * @param selectors the selector string array
     * @return a map containing the parameters
     * @throws Exception any Exception
     */
    public static Map<String, String> parseParams(final String[] selectors)
            throws Exception {
        //the map containing the parsed parameters
        final Map<String, String> params = Maps.newHashMap();

        for (String pairIt : selectors) {
            final String pair = URLDecoder.decode(pairIt, MetLifeConstants
                    .UTF8_CHARSET); //decoding selector
            final String[] paramArray = pair.split(MetLifeConstants
                    .PARAM_DELIMITER); //split into name/value
            if (paramArray.length == 2) { //if the selector doesn't have a
                // name=value format, we ignore it

                final String paramName = paramArray[0];
                final String paramValue = paramArray[1];

                //we are only going to take into account the first parameter
                // with a specific name
                if (!params.containsKey(paramName)) {
                    params.put(paramName, paramValue);
                }
            }
        }

        return params;
    }
}
