package com.onbelay.dealcapture.pricing.curvesfilereader;

import java.util.ArrayList;
import java.util.List;

public  class CurveFileFormat  {

    private List<CurveColumnType> columns = new ArrayList<>();

    public CurveFileFormat() {
        addColumn(CurveColumnType.INDEX_NAME);
        addColumn(CurveColumnType.FREQUENCY_CODE);
        addColumn(CurveColumnType.CURVE_DATE);
        addColumn(CurveColumnType.CURVE_DATE_HOUR_ENDING);
        addColumn(CurveColumnType.OBSERVED_DATE_TIME);
        addColumn(CurveColumnType.CURVE_VALUE);
    }

    protected void addColumn(CurveColumnType columnType) {
        columns.add(columnType);
    }

    public CurveColumnType get(int index) {
        return columns.get(index);
    }

    public String[] getAsArray() {

        String[] array = new String[columns.size()];
        for (int i=0 ; i< columns.size(); i++) {
            CurveColumnType type = columns.get(i);
            array[i] = type.getCode();
        }
        return array;
    }

    public int length() {
        return columns.size();
    }
}
