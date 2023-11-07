package com.onbelay.dealcapture.formulas.parser;


import com.onbelay.core.query.parsing.CloseBracketHolder;
import com.onbelay.core.query.parsing.OpenBracketHolder;
import com.onbelay.dealcapture.formulas.model.*;
import com.onbelay.dealcapture.formulas.tokens.CurrencyNameToken;
import com.onbelay.dealcapture.formulas.tokens.PriceIndexNameToken;
import com.onbelay.dealcapture.formulas.tokens.UnitOfMeasureNameToken;
import com.onbelay.dealcapture.parsing.enums.OperatorType;
import com.onbelay.dealcapture.parsing.exceptions.ParsingException;
import com.onbelay.dealcapture.parsing.model.*;
import com.onbelay.dealcapture.parsing.parser.TemplateParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormulaParser  {
    private static final Logger logger = LogManager.getLogger();

    private String expressionText;
    private ReservedWordManager reservedWordManager;

    public FormulaParser(
            ReservedWordManager reservedWordManager,
            String expressionText) {

        this.reservedWordManager = reservedWordManager;
        this.expressionText = expressionText;
    }

    public FormulaExpression parse() {
        List<IsToken> tokens = new TemplateParser()
                .with(expressionText)
                .read()
                .map(this::validateBrackets)
                .map(reservedWordManager::map)
                .map(this::extractPriceValue)
                .map(this::extractOperators)
                .map(this::extractIndexElements)
                .map(this::extractSubLists)
                .map(this::extractSubExpressions)
                .get();

        return newFormulaExpression(tokens);
    }

    protected FormulaExpression newFormulaExpression(List<IsToken> tokensIn) {
        ArrayList<FormulaElement> elements = new ArrayList<>();
        List<IsToken> unknownList = new ArrayList<>();

        for (IsToken token : tokensIn) {
            if (token instanceof  FormulaElement element)
                elements.add(element);
            else
                unknownList.add(token);
        }
        if (unknownList.size() == 0)
            return new FormulaExpression(elements);
        else
            return new FormulaExpression(elements, unknownList);

    }

    public ReservedWordManager getReservedWordManager() {
        return reservedWordManager;
    }

    public String getExpressionText() {
        return expressionText;
    }

    private List<IsToken> validateBrackets(List<IsToken> tokensIn) {

        int totalOpenBrackets = 0;
        int totalCloseBrackets = 0;
		for (IsToken token : tokensIn) {
            if (token.isProcessed())
                continue;

            if (token instanceof CloseBracketHolder) {
                totalCloseBrackets++;
            }
            if (token instanceof OpenBracketHolder) {
                totalOpenBrackets++;
            }
        }

		if (totalCloseBrackets != totalOpenBrackets) {
            logger.error("Mismatched brackets. Total Open: " + totalOpenBrackets + " Total Close: " + totalCloseBrackets);
            throw new ParsingException("Mismatched brackets. Total Open: " + totalOpenBrackets + " Total Close: " + totalCloseBrackets);
        }
        return tokensIn;
    }


    protected List<IsToken> extractPriceValue(List<IsToken> tokensIn) {
        ArrayList<IsToken> tokensOut = new ArrayList<>();
        for (int i=0; i < tokensIn.size(); i++) {
            IsToken current = tokensIn.get(i);
            if (current.isProcessed())
                continue;
            if (current instanceof NumberToken number) {
                int rangeCheck = i + 3;
                if (rangeCheck >= tokensIn.size()){
                    tokensOut.add(current);
                    continue;
                } else {
                    IsToken next = tokensIn.get((i + 1));
                    IsToken nextNext = tokensIn.get((i + 2));
                    IsToken nextNextNext = tokensIn.get((i + 3));
                    if (next instanceof CurrencyNameToken currency &&
                        nextNext instanceof OperatorToken operator &&
                        operator.getOperatorType() == OperatorType.DIVIDE &&
                        nextNextNext instanceof UnitOfMeasureNameToken unitOfMeasure) {
                        current.setProcessed(true);
                        next.setProcessed(true);
                        nextNext.setProcessed(true);
                        nextNextNext.setProcessed(true);
                            tokensOut.add(
                                    new PriceElement(
                                            number.getValue(),
                                            currency.getCurrencyType(),
                                            unitOfMeasure.getUnitOfMeasureType())
                            );
                        }
                    }
                } else {
                tokensOut.add(current);
            }
        }
        return tokensOut;
    }

    protected List<IsToken> extractOperators(List<IsToken> tokensIn) {
        ArrayList<IsToken> tokensOut = new ArrayList<>();
        for (int i=0; i < tokensIn.size(); i++) {
            IsToken current = tokensIn.get(i);
            if (current.isProcessed())
                continue;
            if (current instanceof OperatorToken operatorToken) {
                current.setProcessed(true);
                tokensOut.add(new FormulaOperator(operatorToken.getOperatorType()));
            } else {
                tokensOut.add(current);
            }
        }
        return tokensOut;
    }


    protected List<IsToken> extractSubExpressions(List<IsToken> tokensIn) {
        ArrayList<IsToken> tokensOut = new ArrayList<>();
        for (int i=0; i < tokensIn.size(); i++) {
            IsToken current = tokensIn.get(i);
            if (current.isProcessed())
                continue;
            if (current instanceof ListToken listToken) {
                current.setProcessed(true);
                tokensOut.add(newFormulaExpression(
                                listToken.getTokens()));
            } else {
                tokensOut.add(current);
            }
        }
        return tokensOut;
    }


    protected List<IsToken> extractIndexElements(List<IsToken> tokensIn) {
        ArrayList<IsToken> tokensOut = new ArrayList<>();
        for (int i=0; i < tokensIn.size(); i++) {
            IsToken current = tokensIn.get(i);
            if (current.isProcessed())
                continue;
            if (current instanceof PriceIndexNameToken indexToken) {
                current.setProcessed(true);
                tokensOut.add(new IndexElement(indexToken.getWord()));
            } else {
                tokensOut.add(current);
            }
        }
        return tokensOut;
    }

    protected List<IsToken> extractSubLists(List<IsToken> tokensIn) {
        List<IsToken> tokensOut = tokensIn
                .stream()
                .map(c-> c.createCopy())
                .collect(Collectors.toList());

        // Find last open Bracket
        boolean stillFindingBrackets = true;
        while (stillFindingBrackets) {
            stillFindingBrackets = false;
            int openBracketIndex = -1;

            for (int i = tokensOut.size() - 1; i != 0; i--) {
                IsToken current = tokensOut.get(i);
                if (current.isProcessed())
                    continue;
                if (current instanceof OpenBracketToken) {
                    openBracketIndex = i;
                    break;
                }
            }

            if (openBracketIndex != -1) {
                stillFindingBrackets = true;
                tokensOut.add(
                        openBracketIndex,
                        extractSubList(tokensOut, openBracketIndex));
            }
        }

        return clean(tokensOut);
    }

    protected List<IsToken> clean(List<IsToken> tokensIn) {
        return tokensIn
                .stream()
                .filter(c-> c.isProcessed() == false)
                .collect(Collectors.toList());
    }

    protected ListToken extractSubList(
            List<IsToken> tokensIn,
            int start) {

        ArrayList<IsToken> subList = new ArrayList<>();
        IsToken openBracket = tokensIn.get(start);
        openBracket.setProcessed(true);

        for (int i=start+1; i < tokensIn.size(); i++) {
            IsToken current = tokensIn.get(i);
            if (current.isProcessed())
                continue;
            if (current instanceof CloseBracketToken) {
                current.setProcessed(true);
                break;
            }
            subList.add(current.createCopy());
            current.setProcessed(true);
        }
        return new ListToken(subList);

    }


}
