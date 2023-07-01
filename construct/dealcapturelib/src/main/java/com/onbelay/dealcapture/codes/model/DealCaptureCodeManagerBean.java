package com.onbelay.dealcapture.codes.model;


import com.onbelay.shared.codes.model.SharedCodeManagerBean;

public class DealCaptureCodeManagerBean extends SharedCodeManagerBean {

    public DealCaptureCodeManagerBean() {
        addCodeEntity(DealStatusCodeEntity.codeFamily, "DealStatusCodeEntity");
        addCodeEntity(DealTypeCodeEntity.codeFamily, "DealTypeCodeEntity");
        addCodeEntity(FrequencyCodeEntity.codeFamily, "FrequencyCodeEntity");
        addCodeEntity(UnitOfMeasureCodeEntity.codeFamily, "UnitOfMeasureCodeEntity");
    }


}
