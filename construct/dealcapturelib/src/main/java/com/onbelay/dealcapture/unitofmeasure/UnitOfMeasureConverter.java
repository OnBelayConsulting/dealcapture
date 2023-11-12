package com.onbelay.dealcapture.unitofmeasure;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UnitOfMeasureConverter {

    private static Map<String, Conversion> conversions = new HashMap<>();

    static {
        conversions.put(
                createKey(
                        UnitOfMeasureCode.MMBTU,
                        UnitOfMeasureCode.GJ),
                new Conversion(
                        BigDecimal.valueOf(0.947817),
                        UnitOfMeasureCode.MMBTU,
                        UnitOfMeasureCode.GJ));

    }

    /**
     * Create a key for conversion look up
     * @param to    - unitOfMeasure to
     * @param from  - unitOfMeasure from
     * @return
     */
    protected static String createKey(
            UnitOfMeasureCode to,
            UnitOfMeasureCode from) {
        return to.getCode() + "_" + from.getCode();
    }

    /**
     * Find a conversion that will take a value in from and converted to to
     * @param to unitOfMeasure to
     * @param from - unitOfMeasure from
     * @return a Conversion that will change the unit of measure to or
     * null if no conversion exists.
     */
    public static Conversion findConversion(
            UnitOfMeasureCode to,
            UnitOfMeasureCode from) {
        Conversion conversion = conversions.get(
                createKey(
                        to,
                        from));

        if (conversion != null) {
            return conversion;
        } else  {

            Conversion other =  conversions.get(
                                            createKey(
                                                    from,
                                                    to));
            if (other != null)
                return other.getInversion();
        }
        return null;
    }

}
