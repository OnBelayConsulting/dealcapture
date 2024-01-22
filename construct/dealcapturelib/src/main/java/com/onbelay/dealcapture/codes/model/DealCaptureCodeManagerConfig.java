package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.CodeManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DealCaptureCodeManagerConfig implements InitializingBean {

    @Autowired
    private CodeManager codeManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        codeManager.addCodeEntity(DealStatusCodeEntity.codeFamily, "DealStatusCodeEntity");
        codeManager.addCodeEntity(DealTypeCodeEntity.codeFamily, "DealTypeCodeEntity");

    }
}
