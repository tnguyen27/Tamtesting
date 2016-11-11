package com.metlife.global.logiclesstemplates.processors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.logiclesstemplates.contextprocessors.AbstractConfigurationContextProcessor;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.configuration.XCQBConfiguration;
import com.xumak.base.configuration.XCQBConfigurationProvider;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Reference;


/**
 * ColumnsAndRowsContextProcessor.
 *
 * Create lists from column nodes to organize the table content.
 *
 * This context processor requires four configuration properties:
 *
 *
 *  xk_numberOfColumns:
 *      String that indicates the number of columns of the table.
 *
 *      Ex:
 *          xk_numberOfColumns = 2
 *
 *
 *  xk_contentColumn:
 *      String that references the column items in the content model,
 *      this is where the copied content is going to be included.
 *
 *      Notice that there may be more than one content column and the
 *      content column must always include a suffix "n" in the content
 *      model where "n" is an integer representing the number of the column.
 *
 *      Ex:
 *          xk_contentColumn = content.columnItems
 *
 *          If the xk_numberOfColumns property is set to 2 then the context processor
 *          will look for:
 *          content.columnItems1
 *          content.columnItems2
 *
 *
 *  xk_copyToColumn:
 *     String array that includes a reference to the content to copy and
 *     the key that will be used to store the content in each content column
 *     separated by "->".
 *
 *     Notice that this context processor will do this for each column and
 *     expects to find "n" instances of each reference, is the referenced value
 *     is not found then nothing will be copied to the column.
 *
 *     ex:
 *          xk_copyToColumn = content.button->buttonTitle,
 *                            content.link->linkTitle
 *
 *          If the xk_numberOfColumns property is set to 2 then the context processor
 *          will look for:
 *
 *          content.button1
 *          content.link1
 *
 *          content.button2
 *          content.link2
 *
 *          and will copy the content to the corresponding xk_contentColumn.
 *
 *
 *  xk_outputTable:
 *      String with the name which will be used to store the result table in the content model.
 *
 *      Ex:
 *          xk_ouputTable = content.newTable
 *
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer       | Changes
 * 1.0     | 24/06/2016 | Sergio Torres   | Initial Creation
 * 2.0     | 05/08/2016 | Diego Tello     | Refactoring
 * ----------------------------------------------------------------------------
 */

@Component
@Service
public class ColumnsAndRowsContextProcessor
        extends AbstractConfigurationContextProcessor<ContentModel> {

    /**
            * XK_CONTENTCOLUMN.
    */

    public static final String XK_CONTENTCOLUMN = "xk_contentColumn";

    /**
     * XK_COPYTOCOLUMN.
     */
    public static final String XK_COPYTOCOLUMN = "xk_copyToColumn";


    /**
     * XK_NUMBEROFCOLUMNS.
     */
    public static final String XK_NUMBEROFCOLUMNS = "xk_numberOfColumns";

    /**
     * XK_OUTPUTTABLE.
     */
    public static final String XK_OUTPUTTABLE = "xk_outputTable";

    /**
     * ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE.
     */
    private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE =
            "The configuration value does not match the required format: ";

    /**
     * Logger.
     */
    //private final Logger logger = LoggerFactory.getLogger(ColumnsAndRowsContextProcessor2.class);


    /**
     * configurationProvider.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy =
            ReferencePolicy.STATIC)
    private XCQBConfigurationProvider configurationProvider;

    @Override
    public int priority() {
        return Constants.LOW_PRIORITY;
    }

    @Override
    public Set<String> requiredPropertyNames() {
        return Sets.newHashSet(XK_CONTENTCOLUMN, XK_COPYTOCOLUMN, XK_OUTPUTTABLE, XK_NUMBEROFCOLUMNS);
    }

    @Override
    public Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public void process(final SlingHttpServletRequest request, final ContentModel contentModel) throws Exception {
        final Resource resource = request.getResource();
        final XCQBConfiguration configuration = configurationProvider.getFor(resource.getResourceType());

        //Get the required configurations
        final String contentColumnName = configuration.asString(XK_CONTENTCOLUMN);
        final Collection<String> valuesToCopy = configuration.asStrings(XK_COPYTOCOLUMN);
        final String numberOfColumnsReference = configuration.asString(XK_NUMBEROFCOLUMNS);
        final int numberOfColumns = Integer.parseInt(contentModel.getAsString(numberOfColumnsReference));

        final List<Object> contentTable = new ArrayList<>();

        //Go through the columns
        for (int i = 0; i <= numberOfColumns; i++) {
            //Check if the column exists in the content model
            if (contentModel.has(contentColumnName + i)) {
                final Map<String, Object> copyToColumnItems = new HashMap<>();
                //Go through the configured values to be copied into the table
                for (final String valueConfiguration : valuesToCopy) {
                    //Check that the configured value complies with the required format
                    if (valueConfiguration.contains(MetLifeConstants.ARROW_OPERATOR)) {
                        final String[] columns = valueConfiguration.split(MetLifeConstants.ARROW_OPERATOR);
                        //Check that there are only two configured values
                        if (columns.length != 2) {
                            throw new IllegalArgumentException(
                                    ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE + valueConfiguration);
                        }
                        final String itemToCopyName = columns[0];
                        final String destinationItemName = columns[1];
                        //Add the value to the resulting column
                        copyToColumnItems.put(destinationItemName, contentModel.get(itemToCopyName + i));
                    } else {
                        throw new IllegalArgumentException(
                                ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE + valueConfiguration);
                    }
                }
                //Get the content column
                final Collection<Object> contentColumn = Utils.getPropertyAsList(contentModel, contentColumnName + i);
                //Add the values to the column
                contentColumn.add(copyToColumnItems);
                //Add the column to the table
                contentTable.add(contentColumn);
            }
        }

        //Get the output table name
        final String outputTableName = configuration.asString(XK_OUTPUTTABLE);
        //Store the resulting table in the content model
        contentModel.set(outputTableName, contentTable);
    }
}
