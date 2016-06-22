package com.metlife.poc.logiclesstemplates.contextprocessors;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metlife.poc.util.MetLifeConstants;
import com.metlife.poc.util.Utils;
import com.xumak.base.Constants;
import com.xumak.base.templatingsupport.ContentModel;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * EvaluateEqualToConditionContextProcessor*
 * Compare two operands and determine if they are logically identical.
 * In order to compare two operands the following property should be used in
 * xk.config node:
 * Property Name                   Type        Value
 * xk_evaluateEqualsCondition      String[]    left_operand==right_operand,
 * left_op2==right_op2, ...
 *
 * If the logically comparison it is true, the "true" value is stored within
 * a new property
 * called "content.EqualTo_counter", where counter is a number starting from
 * zero corresponding
 * at the first comparison operation, it means, the result of the first
 * comparison will be stored
 * within a property called "content.EqualTo_0", if there are more comparison
 * operations to do, each
 * result will be stored within "content.EqualTo_1 ... content.EqualTo_2 ..."
 * and so on.
 *
 * In order to compare two operands and get a customized value the following
 * property should be used
 * in xk.config node:
 *
 * Property Name                   Type        Value
 * xk_evaluateEqualsCondition      String[]
 * left_operand==right_operand->customized_value
 *
 * If the logically comparison it is true, the "customized_value" is stored
 * within a property called
 * "content.EqualTo_counter".
 *
 *
 * When the result of comparison is false any property is not saved.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 24/08/2015 | eburrion@xumak.com    | Initial Creation.
 * 1.0     | 02/18/2016 | jmorataya@xumak.com   | Adapted to metlife (latam
 * original)
 * ----------------------------------------------------------------------------
 */


@Component
@Service
public class EvaluateEqualToConditionContextProcessor extends
        AbstractConfigurationContextProcessor {

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
        return Sets.newHashSet(MetLifeConstants.XK_EVALUATE_EQUALS_CONDITION);
    }

    /**
     * getOptionalConfigurations.
     * @return Collection<String>
     */
    @Override
    public final Collection<String> getOptionalConfigurations() {
        return Lists.newArrayList();
    }

    @Override
    public final void process(final SlingHttpServletRequest request, final
    ContentModel contentModel) throws Exception {
        final List<String> operationsList = (List<String>)
                Utils.getPropertyAsList(contentModel,
                        Constants.CONFIG_PROPERTIES_KEY
                        + Constants.DOT
                              + MetLifeConstants.XK_EVALUATE_EQUALS_CONDITION);
        if (operationsList.size() > 0) {
            int count = 0;
            for (String currentOperation : operationsList) {
                if (currentOperation.split(
                        MetLifeConstants.EQUAL_TO_OPERATOR).length == 2) {
                    final String leftOperand = currentOperation.split(
                            MetLifeConstants.EQUAL_TO_OPERATOR)[0];
                    if (contentModel.has(leftOperand)) {
                        final String leftOperandVal = contentModel.getAsString(
                                leftOperand);
                        final String rightCompositeOperand = currentOperation.
                                split(MetLifeConstants.EQUAL_TO_OPERATOR)[1];
                        final  String rightOperand = getRightOperand(
                                rightCompositeOperand);
                        final String resultValue = getValueToReturn(
                                rightCompositeOperand);

                        if (contentModel.has(rightOperand)) {
                            // both operands are properties
                            if ((contentModel.get(leftOperand)).equals(
                                    contentModel.get(rightOperand))) {
                                contentModel.set(Constants.RESOURCE_CONTENT_KEY
                                        + Constants.DOT
                                        + MetLifeConstants.EQUAL_TO_RESULT
                                        + count, resultValue);
                            }
                        } else {
                            // Right operand it is a string
                            if (leftOperandVal.equals(rightOperand)) {
                                contentModel.set(Constants.RESOURCE_CONTENT_KEY
                                        + Constants.DOT
                                        + MetLifeConstants.EQUAL_TO_RESULT
                                        + count, resultValue);
                            }
                        }
                    }
                }
                count++;
            }
        }
    }

    /**
     * Get right operand.
     * @param rightOperand String.
     * @return String.
     */
    private String getRightOperand(final String rightOperand) {
        // left_operand == right_operand->value_to_return
        if (rightOperand.split(MetLifeConstants.ARROW_OPERATOR).length == 2) {
            return rightOperand.split(MetLifeConstants.ARROW_OPERATOR)[0];
        } else {
            // left_operand == right_operand
            return rightOperand;
        }
    }

    /**
     * Get value to return.
     * @param rightOperand String.
     * @return String.
     */
    private String getValueToReturn(final String rightOperand) {
        // left_operand == right_operand, where right_operand is like
        // 'right_operand->value_to_return'
        if (rightOperand.split(MetLifeConstants.ARROW_OPERATOR).length == 2) {
            return rightOperand.split(MetLifeConstants.ARROW_OPERATOR)[1];
        } else {
            return Constants.TRUE;
        }
    }
}
