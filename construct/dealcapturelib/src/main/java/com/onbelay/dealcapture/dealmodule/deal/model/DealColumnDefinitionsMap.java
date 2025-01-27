package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.query.model.ColumnDefinitions;

import java.util.HashMap;
import java.util.Map;

public class DealColumnDefinitionsMap {

    private Map<String, ColumnDefinitions> columnDefinitionsMap = new HashMap<String, ColumnDefinitions>();

    public ColumnDefinitions getColumnDefinitions(String entityName) {
        return columnDefinitionsMap.get(entityName);
    }

    public void putColumnDefinitions(String entityName, ColumnDefinitions columnDefinitions) {
        columnDefinitionsMap.put(entityName, columnDefinitions);
    }

}
