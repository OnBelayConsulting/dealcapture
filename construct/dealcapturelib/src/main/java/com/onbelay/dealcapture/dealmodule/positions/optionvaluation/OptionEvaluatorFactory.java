package com.onbelay.dealcapture.dealmodule.positions.optionvaluation;

public class OptionEvaluatorFactory {

    public static OptionEvaluator createOptionEvaluator() {
        return new BlackScholesOptionEvaluator();
    }

}
