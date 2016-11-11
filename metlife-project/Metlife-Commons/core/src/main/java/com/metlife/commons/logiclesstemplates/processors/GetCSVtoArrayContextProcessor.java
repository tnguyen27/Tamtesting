package com.metlife.commons.logiclesstemplates.processors;


import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * GetCSVtoArrayContextProcessor
 * This context processor validate the paths of the files in the <b>xk_csvToArray</b> and me an Object whit
 * the data conteined in the file.
 * The criteria to evaluate the property value is the following
 *      * Exists?
 *      * Has content?
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 29/06/2016 | Roger Jimenez   | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component
@Service
public class GetCSVtoArrayContextProcessor extends AbstractConfigurationContextProcessor {

    private static final String EMPTY_TEXT = "";
    private static final String JCR_DATA = "jcr:data";
    private static final String SPLIT_CHAR = ",";
    private static final String COMMA_SYMBOL = ",";
    private static final String UNDERSCORE = "_";
    private static final String REPLACE_STRING = "\"";
    private static final String CSV_TABLE_KEY = "TableCSV";
    private static final String XK_CSVTOARRAY = "xk_csvToArray";
    private static final int INDEX_CONTENT = 8;
    private static final String VAR_DIR = "/var";


    @Override
    protected final boolean mustExist() {
        return false;
    }


    @Override
    public final int priority() {
        return Constants.LOW_PRIORITY - Constants.HIGH_PRIORITY;
    }

    @Override
    public final Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_CSVTOARRAY);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest slingHttpServletRequest, final ContentModel contentModel)
            throws Exception {
        final Map<String, Object> contentObject = (Map<String, Object>)
                contentModel.get(Constants.RESOURCE_CONTENT_KEY);

        int indexTables = 0;

        final List<String> operationsList = (List<String>)
                Utils.getPropertyAsList(contentModel, Constants.CONFIG_PROPERTIES_KEY + Constants.DOT  + XK_CSVTOARRAY);

        for (final String currentOperation : operationsList) {
            if (contentModel.has(currentOperation)) {
                String csvPath = null;
                csvPath = (String) contentModel.getAsString(currentOperation);

                final ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
                final AssetManager manager = resourceResolver.adaptTo(AssetManager.class);
                csvPath = VAR_DIR + csvPath.substring(INDEX_CONTENT);
                final Asset csvfileBinary = manager.getAssetForBinary(csvPath);

                final Object valor = csvfileBinary.getOriginal().getProperties().get(JCR_DATA);
                final String resultr = IOUtils.toString((InputStream) valor, StandardCharsets.UTF_8);
                final StringReader stringReader = new StringReader(resultr);
                final BufferedReader bufferedReader = new BufferedReader(stringReader);
                String line = EMPTY_TEXT;

                line = bufferedReader.readLine();
                final List<List<List>> result = new ArrayList<>();
                final List<List> lineList = new ArrayList<>();
                while (line != null) {
                    List<String> mapline = new ArrayList<>();
                    final String[] country = line.split(SPLIT_CHAR);
                    for (int x = 0; x < country.length - 1; x++) {
                        if (country[x].startsWith(REPLACE_STRING)) {
                            final String datafix = country[x] + COMMA_SYMBOL + country[x + 1];
                            country[x] = datafix;
                            country[x + 1] = EMPTY_TEXT;
                        }
                    }
                    final String[] finalList = new String[country.length];
                    int index = 0;
                    for (int x = 0; x < country.length; x++) {
                        if (country[x].equals(EMPTY_TEXT)) {
                            continue;
                        } else if (country[x].contains(REPLACE_STRING)) {
                            finalList[index] = country[x].replace(REPLACE_STRING, EMPTY_TEXT);
                            index++;
                        } else {
                            finalList[index] = country[x];
                            index++;
                        }
                    }
                    mapline = new LinkedList<String>(Arrays.asList(finalList));
                    final List<String> listNoBlanks = new ArrayList<>();
                    for (int x = 0; x < mapline.size(); x++) {
                        if (null != mapline.get(x) && !mapline.get(x).isEmpty()) {
                            listNoBlanks.add(mapline.get(x));
                        }
                    }
                    lineList.add(listNoBlanks);
                    line = bufferedReader.readLine();
                }
                result.add(lineList);
                contentObject.put(CSV_TABLE_KEY + UNDERSCORE + indexTables, result);
                indexTables++;
            }
        }

    }

}
