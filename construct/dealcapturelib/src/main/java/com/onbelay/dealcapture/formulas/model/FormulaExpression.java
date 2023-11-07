package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.busmath.model.CalculatedEntity;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.formulas.exceptions.FormulaEvaluationException;
import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormulaExpression extends BaseElement implements FormulaElement, FormulaOperand, IsToken {

    private List<FormulaElement> elements = new ArrayList<>();
    private List<IsToken> unknownList = new ArrayList<>();

    public FormulaExpression(List<FormulaElement> elements) {
        this.elements = elements;
    }

    public FormulaExpression(
            List<FormulaElement> elements,
            List<IsToken> unknownList) {
        this.elements = elements;
        this.unknownList = unknownList;
    }

    public void collectRiskFactors() {

    }

    public List<FormulaElement> getElements() {
        return elements;
    }

    @Override
    public boolean isInError() {
        return unknownList.size() > 0;
    }

    @Override
    public CalculatedEntity evaluate(EvaluationContext context) {

        if (elements.isEmpty())
            return new Price(CalculatedErrorType.ERROR);

        if (elements.size() == 1)
            if (elements.get(0) instanceof FormulaOperand operand) {
                return operand.evaluate(context);
            } else {
                throw new FormulaEvaluationException("Invalid operand");
            }

        if (elements.size() > 2) {
            List<FormulaElement> copiedElements = elements
                    .stream()
                    .map(c-> c.duplicate())
                    .collect(Collectors.toList());

            boolean hasMore = true;
            while (hasMore) {
                hasMore = false;

                FormulaOperator currentOperator = null;
                int currentPosition = -1;

                for (int i=0; i < copiedElements.size(); i++) {
                    FormulaElement element = copiedElements.get(i);
                    if (element.isProcessed())
                        continue;

                    if (element instanceof FormulaOperator operator) {
                        if (currentOperator == null) {
                            currentOperator = operator;
                        } else {
                            if (operator.getOperatorType().isHigherPrecedenceThan(currentOperator.getOperatorType())) {
                                currentOperator = operator;
                                currentPosition = i;
                            }
                        }

                    }
                }
                if (currentOperator != null) {
                    hasMore = true;
                    if (currentPosition == 0)
                        throw new FormulaEvaluationException("Operator at beginning");
                    if ((currentPosition + 1) >= copiedElements.size()) {
                        throw new FormulaEvaluationException("Missing second operand");
                    }
                    FormulaOperator operator =  (FormulaOperator) copiedElements.get(currentPosition);
                    FormulaElement prev = copiedElements.get(currentPosition-1);
                    FormulaElement next = copiedElements.get(currentPosition+1);
                    if (prev instanceof FormulaOperand firstOperand && next instanceof FormulaOperand secondOperand) {
                        prev.setProcessed(true);
                        operator.setProcessed(true);
                        next.setProcessed(true);
                        CalculatedEntity calculatedEntity = operator.apply(
                                context,
                                firstOperand,
                                secondOperand);
                        if (calculatedEntity instanceof Price price)
                            copiedElements.add(currentPosition, new PriceElement(price));
                        else
                            throw new FormulaEvaluationException("not a price");
                    }
                }


            }

            Optional<FormulaElement> elementHolder = copiedElements.stream().filter(c-> c.isProcessed() == false).findFirst();
            if (elementHolder.isPresent())
                return ((FormulaOperand)elementHolder.get()).evaluate(context);
        } else {
            throw new FormulaEvaluationException("Not enough elements");
        }
        throw new FormulaEvaluationException("Not evaluated");
    }

    @Override
    public void collectRiskFactors(
            EvaluationContext context,
            LocalDate marketDate,
            RiskFactorManager riskFactorManager) {

    }


    public boolean hasErrors() {
        return unknownList.size() > 0;
    }

    public List<IsToken> getUnknownList() {
        return unknownList;
    }

    @Override
    public IsToken createCopy() {
        return new FormulaExpression(
            elements
                    .stream()
                    .map(c-> c.duplicate())
                    .collect(Collectors.toList()));
    }
}
