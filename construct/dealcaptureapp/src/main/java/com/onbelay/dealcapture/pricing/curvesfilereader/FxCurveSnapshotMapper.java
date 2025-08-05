package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FxCurveSnapshotMapper {

    private FxCurveSnapshot fxCurveSnapshot = new FxCurveSnapshot();


    public FxCurveSnapshot getSnapshot() {
        return fxCurveSnapshot;
    }



    public boolean setPropertyValue(
            CurveColumnType columnType,
            Object value) {


        switch (columnType) {

            case INDEX_NAME -> {
                fxCurveSnapshot.setIndexId(new EntityId((String)value));
                return true;
            }

            case FREQUENCY_CODE -> {
                FrequencyCode code = FrequencyCode.lookUp((String) value);
                fxCurveSnapshot.getDetail().setFrequencyCode(code);
                return true;
            }

            case CURVE_DATE -> {
                fxCurveSnapshot.getDetail().setCurveDate((LocalDate) value);
                return true;
            }

            case CURVE_DATE_HOUR_ENDING -> {
                fxCurveSnapshot.getDetail().setHourEnding((Integer) value);
                return true;
            }

            case OBSERVED_DATE_TIME -> {
                fxCurveSnapshot.getDetail().setObservedDateTime((LocalDateTime) value);
                return true;
            }


            case CURVE_VALUE -> {
                fxCurveSnapshot.getDetail().setCurveValue((BigDecimal) value);
                return true;
            }


            default ->  {
                return false;
            }

        }
    }


}
