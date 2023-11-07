package com.onbelay.dealcapture.formulas.parser;

import com.onbelay.dealcapture.formulas.tokens.FormulaWordToken;
import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.parsing.model.ReservedWordToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseReservedWordManager implements ReservedWordManager {

    protected Map<String, Function<ReservedWordToken, FormulaWordToken>> formulaWordMap = new HashMap<>();

    @Override
    public List<IsToken> map(List<IsToken> tokensIn) {

        ArrayList<IsToken> tokensOut = new ArrayList<>();
        for (IsToken token : tokensIn) {
            if (token instanceof ReservedWordToken reservedWordToken) {
                Function<ReservedWordToken, FormulaWordToken> fn = formulaWordMap.get(reservedWordToken.getWord());
                if (fn != null) {
                    tokensOut.add(fn.apply(reservedWordToken));
                } else {
                    tokensOut.add(token);
                }
            } else {
                tokensOut.add(token);
            }
        }
        return tokensOut;
    }

}
