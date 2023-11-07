package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.busmath.model.CalculatedEntity;
import com.onbelay.dealcapture.parsing.enums.OperatorType;
import com.onbelay.dealcapture.parsing.model.IsToken;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FormulaOperator extends BaseElement implements FormulaElement {

    private OperatorType operatorType;

    private static Map<OperatorType, BiFunction<CalculatedEntity, CalculatedEntity, CalculatedEntity>> opMap = new HashMap<>();

    static {
        opMap.put(OperatorType.ADD, (c,d) -> c.add(d));
        opMap.put(OperatorType.SUBTRACT, (c,d) -> c.subtract(d));
        opMap.put(OperatorType.MULTIPLY, (c,d) -> c.multiply(d));
        opMap.put(OperatorType.DIVIDE, (c,d) -> c.divide(d));
    }

    public FormulaOperator(boolean isInError, String errorCode) {
        super(isInError, errorCode);
    }

    public FormulaOperator(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }


    @Override
    public IsToken createCopy() {
        FormulaOperator formulaOperator = new FormulaOperator(isInError(), getErrorCode());
        formulaOperator.operatorType = this.operatorType;
        return formulaOperator;
    }

    public CalculatedEntity apply(
            EvaluationContext  evaluationContext,
            FormulaOperand firstOperand,
            FormulaOperand secondOperand) {

        return opMap.get(this)
                .apply(
                        firstOperand.evaluate(evaluationContext),
                        secondOperand.evaluate(evaluationContext));
    }
}
