package com.onbelay.dealcapture.pricing.priceCurvesfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.BaseDealSnapshotMapper;
import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.DealColumnType;
import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.PhysicalDealFileFormat;
import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.SourceFileFormat;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceCurve;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CurveSnapshotMapper {

    private PriceCurveSnapshot priceCurveSnapshot = new PriceCurveSnapshot();


    public PriceCurveSnapshot getSnapshot() {
        return priceCurveSnapshot;
    }



    public boolean setPropertyValue(
            CurveColumnType columnType,
            Object value) {


        switch (columnType) {

            case INDEX_NAME -> {
                priceCurveSnapshot.setIndexId(new EntityId((String)value));
                return true;
            }

            case FREQUENCY_CODE -> {
                FrequencyCode code = FrequencyCode.lookUp((String) value);
                priceCurveSnapshot.getDetail().setFrequencyCode(code);
                return true;
            }

            case CURVE_DATE -> {
                priceCurveSnapshot.getDetail().setCurveDate((LocalDate) value);
                return true;
            }

            case CURVE_DATE_HOUR_ENDING -> {
                priceCurveSnapshot.getDetail().setHourEnding((Integer) value);
                return true;
            }

            case OBSERVED_DATE_TIME -> {
                priceCurveSnapshot.getDetail().setObservedDateTime((LocalDateTime) value);
                return true;
            }


            case CURVE_VALUE -> {
                priceCurveSnapshot.getDetail().setCurveValue((BigDecimal) value);
                return true;
            }


            default ->  {
                return false;
            }

        }
    }


}
