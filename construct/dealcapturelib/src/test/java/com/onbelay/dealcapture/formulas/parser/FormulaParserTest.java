package com.onbelay.dealcapture.formulas.parser;

import com.onbelay.dealcapture.formulas.model.FormulaExpression;
import com.onbelay.dealcapture.formulas.model.FormulaOperator;
import com.onbelay.dealcapture.formulas.model.IndexElement;
import com.onbelay.dealcapture.formulas.model.PriceElement;
import com.onbelay.dealcapture.parsing.model.ReservedWordToken;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaParserTest extends DealCaptureSpringTestCase {


    @Test
    public void parsePriceValue() {
        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "10.4 CAD/GJ");
        FormulaExpression expression = formulaParser.parse();

        assertEquals(1, expression.getElements().size());
        assertTrue(expression.getElements().get(0) instanceof PriceElement);
    }


    @Test
    public void parseIlFormedPriceValue() {
        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "10.4 CAD/");
        FormulaExpression expression = formulaParser.parse();
        assertTrue(expression.isInError());
        assertEquals(1, expression.getElements().size());
        assertEquals(2, expression.getUnknownList().size());
    }

    @Test
    public void parseIndexNames() {

        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "AECO");
        FormulaExpression expression = formulaParser.parse();
        assertEquals(1, expression.getElements().size());
        assertTrue(expression.getElements().get(0) instanceof IndexElement);
    }


    @Test
    public void parseBrackettedExpression() {

        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "AECO + ((AECO * 2) - 4 CAD/GJ)");
        FormulaExpression expression = formulaParser.parse();
        assertEquals(3, expression.getElements().size());
        assertTrue(expression.getElements().get(0) instanceof IndexElement);
        assertTrue(expression.getElements().get(1) instanceof FormulaOperator);
        assertTrue(expression.getElements().get(2) instanceof FormulaExpression);
    }


    @Test
    public void parseMissingName() {

        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "AEZZ");
        FormulaExpression expression = formulaParser.parse();
        assertTrue(expression.isInError());
        assertEquals(0, expression.getElements().size());
        assertTrue(expression.getUnknownList().get(0) instanceof ReservedWordToken);
    }


    @Test
    public void parseIndexPlusPrice() {

        FormulaReservedWordManager formulaReservedWordManager = new FormulaReservedWordManager();

        FormulaParser formulaParser = new FormulaParser(
                formulaReservedWordManager,
                "'AECO' + 10.4 CAD/GJ");
        FormulaExpression expression = formulaParser.parse();
        assertEquals(3, expression.getElements().size());
        assertTrue(expression.getElements().get(0) instanceof IndexElement);
        assertTrue(expression.getElements().get(1) instanceof FormulaOperator);
        assertTrue(expression.getElements().get(2) instanceof PriceElement);
    }


}
