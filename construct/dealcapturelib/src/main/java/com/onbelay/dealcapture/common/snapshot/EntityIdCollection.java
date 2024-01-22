package com.onbelay.dealcapture.common.snapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityIdCollection {

    private List<Integer> ids = new ArrayList<>();


    public EntityIdCollection(Integer firstId, Integer secondId) {
        if (firstId != null)
            ids.add(firstId);
        if (secondId != null)
            ids.add(secondId);
    }


    public List<Integer> getList() {
        return ids;
    }
}
