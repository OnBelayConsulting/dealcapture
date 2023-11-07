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
                createKey(UnitOfMeasureCode.GJ, UnitOfMeasureCode.MMBTU),
                new Conversion(UnitOfMeasureCode.GJ, UnitOfMeasureCode.MMBTU, BigDecimal.valueOf(2)));

    }

    protected static String createKey(UnitOfMeasureCode from, UnitOfMeasureCode to) {
        return from.getCode() + "_" + to.getCode();
    }

    public static Conversion findConversion(UnitOfMeasureCode from, UnitOfMeasureCode to) {
        Conversion conversion = conversions.get(
                createKey(
                        from,
                        to));

        if (conversion == null) {
            return conversions.get(
                    createKey(to, from));
        } else{
            return conversion;
        }
    }

}
