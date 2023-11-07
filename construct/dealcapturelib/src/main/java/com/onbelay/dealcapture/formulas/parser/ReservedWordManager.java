package com.onbelay.dealcapture.formulas.parser;

import com.onbelay.dealcapture.parsing.model.IsToken;

import java.util.List;

public interface ReservedWordManager {

    public List<IsToken> map(List<IsToken> tokensIn);

}
