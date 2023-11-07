package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenParserTest extends DealCaptureSpringTestCase {

    @Test
    public void parsePriceValue() {

        TokenParser parser = new TokenParser("1.3 CAD/GJ");
        List<IsToken> tokens = parser.parse();
        assertEquals(4, tokens.size());


    }

}
