package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.pricing.snapshot.CurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public class CurveSnapshotMapper <T extends CurveSnapshot> {

    private Supplier<T> supplier;

    private T curveSnapshot;


    public T getSnapshot() {
        return curveSnapshot;
    }

    public CurveSnapshotMapper(Supplier<T> supplier) {
        this.supplier = supplier;
        curveSnapshot = supplier.get();
    }

    public boolean setPropertyValue(
            CurveColumnType columnType,
            Object value) {


        switch (columnType) {

            case INDEX_NAME -> {
                curveSnapshot.setIndexId(new EntityId((String)value));
                return true;
            }

            case FREQUENCY_CODE -> {
                FrequencyCode code = FrequencyCode.lookUp((String) value);
                curveSnapshot.getDetail().setFrequencyCode(code);
                return true;
            }

            case CURVE_DATE -> {
                curveSnapshot.getDetail().setCurveDate((LocalDate) value);
                return true;
            }

            case CURVE_DATE_HOUR_ENDING -> {
                curveSnapshot.getDetail().setHourEnding((Integer) value);
                return true;
            }

            case OBSERVED_DATE_TIME -> {
                curveSnapshot.getDetail().setObservedDateTime((LocalDateTime) value);
                return true;
            }


            case CURVE_VALUE -> {
                curveSnapshot.getDetail().setCurveValue((BigDecimal) value);
                return true;
            }


            default ->  {
                return false;
            }

        }
    }


}
