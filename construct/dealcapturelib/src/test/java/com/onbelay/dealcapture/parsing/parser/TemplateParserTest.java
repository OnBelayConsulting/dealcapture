package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.formulas.tokens.PriceIndexNameToken;
import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.parsing.model.NumberToken;
import com.onbelay.dealcapture.parsing.model.OperatorToken;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateParserTest extends DealCaptureSpringTestCase {


    @Test
    public void parseNumber() {
        List<IsToken> tokenList = new TemplateParser()
                .with("10.4")
                .read()
                .get();
        assertEquals(1, tokenList.size());
        assertTrue(tokenList.get(0) instanceof NumberToken);
    }

    @Test
    public void parseIndexNames() {

        TestReservedWordNameManager testReservedWordNameManager = new TestReservedWordNameManager();

        List<IsToken> tokenList = new TemplateParser()
                .with("AECO")
                .read()
                .map(testReservedWordNameManager::map)
                .get();
        assertEquals(1, tokenList.size());
        assertTrue(tokenList.get(0) instanceof PriceIndexNameToken);
    }


    @Test
    public void parseIndexPlusPrice() {

        TestReservedWordNameManager testReservedWordNameManager = new TestReservedWordNameManager();

        List<IsToken> tokenList = new TemplateParser()
                .with("'AECO' + 10.4")
                .read()
                .map(testReservedWordNameManager::map)
                .get();
        assertEquals(3, tokenList.size());
        assertTrue(tokenList.get(0) instanceof PriceIndexNameToken);
        assertTrue(tokenList.get(1) instanceof OperatorToken);
        assertTrue(tokenList.get(2) instanceof NumberToken);
    }


}
