package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.AbstractCodeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "FREQUENCY_CODE")
public class FrequencyCodeEntity extends AbstractCodeEntity {

    public static String codeFamily = "frequencyCode";

}
