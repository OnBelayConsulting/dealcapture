package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.CodeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DealCaptureCodeManagerConfig {

    @Primary
    @Bean
    public CodeManager codeManager() {
        return new DealCaptureCodeManagerBean();
    }

}
