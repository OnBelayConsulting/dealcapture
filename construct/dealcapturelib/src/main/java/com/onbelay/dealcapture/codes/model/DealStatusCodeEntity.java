package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.AbstractCodeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DEAL_STATUS_CODE")
public class DealStatusCodeEntity extends AbstractCodeEntity {

    public static String codeFamily = "dealStatusCode";

}
