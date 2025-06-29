package com.onbelay.dealcapture.dealmodule.positions.optionvaluation;

import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Black-Scholes formula gives a theoretical estimate of
 * the price of European-style options.
 *
 * The model was first articulated by Fischer Black and Myron Scholes in
 * their 1973 paper, "The Pricing of Options and Corporate Liabilities",
 * published in the Journal of Political Economy. They derived a stochastic
 * partial differential equation, now called the Black-Scholes equation,
 * which governs the price of the option over time.
 *
 * This class was derived from Bret Blackford's BlackScholesFormula class.
 *
 * @author mblackford - M. Bret Blackford (credit to Dhruba Bandopadhyay)
 *
 */
public class BlackScholesOptionEvaluator implements OptionEvaluator{
    private static final Logger logger = LogManager.getLogger();

    // The Abramowitz & Stegun (1964) numerical approximation
    // below uses six constant values in its formula.
    private static final double P = 0.2316419;
    private static final double B1 = 0.319381530;
    private static final double B2 = -0.356563782;
    private static final double B3 = 1.781477937;
    private static final double B4 = -1.821255978;
    private static final double B5 = 1.330274429;


    /**
     * @param callPutType = Call or Put
     * @param underlyingPrice - spot price for underlying
     * @param strikePrice - option strike price
     * @param timeToExpire - time in years until option expires
     * @param interestRate - risk-free annual interest rate
     * @param volatility volatility of returns on underlying
     */
    @Override
    public OptionResult evaluate(
            OptionTypeCode callPutType,
            Double underlyingPrice,
            Double strikePrice,
            Double timeToExpire,
            Double interestRate,
            Double volatility) {


        double blackScholesOptionPrice = 0.0;

        if (callPutType == OptionTypeCode.CALL) {
            double cd1 = cumulativeDistribution(
                    d1(
                            underlyingPrice,
                            strikePrice,
                            interestRate,
                            timeToExpire,
                            volatility)
            );

            double cd2 = cumulativeDistribution(
                    d2(
                            underlyingPrice,
                            strikePrice,
                            interestRate,
                            timeToExpire,
                            volatility));

            blackScholesOptionPrice = underlyingPrice * cd1 - strikePrice * Math.exp(-interestRate * timeToExpire) * cd2;
        } else {
            double cd1 = cumulativeDistribution(
                    -1 * d1(
                            underlyingPrice,
                            strikePrice,
                            interestRate,
                            timeToExpire,
                            volatility)
            );

            double cd2 = cumulativeDistribution(
                    -1 * d2(
                            underlyingPrice,
                            strikePrice,
                            interestRate,
                            timeToExpire,
                            volatility));

            blackScholesOptionPrice = strikePrice * Math.exp(-interestRate * timeToExpire) * cd2 - underlyingPrice * cd1;
        }

        logger.debug("OptionPrice:" + blackScholesOptionPrice);

        return new OptionResult(blackScholesOptionPrice);

    }

    /**
     *
     * @param s
     *            = Spot price of underlying stock/asset
     * @param k
     *            = Strike price
     * @param r
     *            = Risk free annual interest rate continuously compounded
     * @param t
     *            = Time in years until option expiration (maturity)
     * @param v
     *            = Implied volatility of returns of underlying stock/asset
     * @return
     */
    private static double d1(double s, double k, double r, double t, double v) {

        double top = Math.log(s / k) + (r + Math.pow(v, 2) / 2) * t;
        double bottom = v * Math.sqrt(t);

        return top / bottom;
    }

    /**
     *
     * @param s
     *            = Spot price of underlying stock/asset
     * @param k
     *            = Strike price
     * @param r
     *            = Risk free annual interest rate continuously compounded
     * @param t
     *            = Time in years until option expiration (maturity)
     * @param v
     *            = Implied volatility of returns of underlying stock/asset
     * @return
     */
    private static double d2(double s, double k, double r, double t, double v) {
        return d1(s, k, r, t, v) - v * Math.sqrt(t);
    }

    private static double cumulativeDistribution(double x) {

        double t = 1 / (1 + P * Math.abs(x));
        double t1 = B1 * Math.pow(t, 1);
        double t2 = B2 * Math.pow(t, 2);
        double t3 = B3 * Math.pow(t, 3);
        double t4 = B4 * Math.pow(t, 4);
        double t5 = B5 * Math.pow(t, 5);
        double b = t1 + t2 + t3 + t4 + t5;

        double snd = standardNormalDistribution(x); //for testing
        double cd = 1 - (snd * b);

        double resp = 0.0;
        if( x < 0 ) {
            resp = 1 - cd;
        } else {
            resp = cd;
        }

        //return x < 0 ? 1 - cd : cd;
        return resp;
    }


    /**
     * The Abramowitz & Stegun numerical approximation above uses six constant
     * values in its formula. However it also relies on another function in turn
     * � the standard normal probability density function (PDF)
     *
     * @param x
     * @return
     */
    private static double standardNormalDistribution(double x) {

        double top = Math.exp(-0.5 * Math.pow(x, 2));
        double bottom = Math.sqrt(2 * Math.PI);
        double resp = top / bottom;

        return resp;
    }

}
