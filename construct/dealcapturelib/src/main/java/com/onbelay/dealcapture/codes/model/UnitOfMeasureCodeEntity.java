package com.onbelay.dealcapture.codes.model;

import com.onbelay.core.codes.model.AbstractCodeEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "UNIT_OF_MEASURE_CODE")
public class UnitOfMeasureCodeEntity extends AbstractCodeEntity {

    public static String codeFamily = "unitOfMeasureCode";

}
