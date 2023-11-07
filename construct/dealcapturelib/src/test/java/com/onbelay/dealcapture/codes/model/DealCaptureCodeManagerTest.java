package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.CodeManager;
import com.onbelay.core.codes.snapshot.CodeLabel;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.codes.model.CurrencyCodeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealCaptureCodeManagerTest extends DealCaptureSpringTestCase {

    @Autowired
    private CodeManager codeManager;

    @Test
    public void fetchCodes() {
        List<CodeLabel> labels = codeManager.findCodeLabels(CurrencyCodeEntity.codeFamily);
        assertEquals(2, labels.size());
    }

}
