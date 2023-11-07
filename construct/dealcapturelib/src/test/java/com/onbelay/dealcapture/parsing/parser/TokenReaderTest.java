package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.parsing.enums.OperatorType;
import com.onbelay.dealcapture.parsing.enums.TokenType;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenReaderTest extends DealCaptureSpringTestCase {

    @Test
    public void readPriceTokens() {

        TokenReader reader = new TokenReader("2.3 CAD/GJ");

        TokenType firstType = reader.getNextType();
        reader.readNumber();
        assertEquals(TokenType.WHITE_SPACE, reader.getNextType());
        reader.read();
        assertEquals(TokenType.LETTER, reader.getNextType());
        reader.readReservedWord();
        assertEquals(TokenType.OPERATOR, reader.getNextType());
        OperatorType operatorType = reader.readOperator();
        assertEquals(TokenType.LETTER, reader.getNextType());
        String reservedWordToken = reader.readReservedWord();
        assertEquals("GJ", reservedWordToken);




    }

}
