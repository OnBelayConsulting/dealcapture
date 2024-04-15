package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.AbstractCodeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DEAL_TYPE_CODE")
public class DealTypeCodeEntity extends AbstractCodeEntity {

    public static String codeFamily = "dealTypeCode";

}
