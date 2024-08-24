package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PositionGenerationResult {


    protected List<DealPositionSnapshot> dealPositionSnapshots = new ArrayList<>();

    protected List<CostPositionSnapshot> costPositionSnapshots = new ArrayList<>();

    protected List<DealHourlyPositionSnapshot> dealHourlyPositionSnapshots = new ArrayList<>();

    public List<DealPositionSnapshot> getDealPositionSnapshots() {
        return dealPositionSnapshots;
    }

    public void setDealPositionSnapshots(List<DealPositionSnapshot> dealPositionSnapshots) {
        this.dealPositionSnapshots = dealPositionSnapshots;
    }

    public List<CostPositionSnapshot> getCostPositionSnapshots() {
        return costPositionSnapshots;
    }

    public void setCostPositionSnapshots(List<CostPositionSnapshot> costPositionSnapshots) {
        this.costPositionSnapshots = costPositionSnapshots;
    }

    public void addDealHourlyPositionSnapshot(DealHourlyPositionSnapshot dealHourlyPositionSnapshot) {
        dealHourlyPositionSnapshots.add(dealHourlyPositionSnapshot);
    }

    public List<DealHourlyPositionSnapshot> getDealHourlyPositionSnapshots() {
        return dealHourlyPositionSnapshots;
    }

    public void setDealHourlyPositionSnapshots(List<DealHourlyPositionSnapshot> dealHourlyPositionSnapshots) {
        this.dealHourlyPositionSnapshots = dealHourlyPositionSnapshots;
    }

    public void addDealPositionSnapshot(DealPositionSnapshot positionSnapshot) {
        dealPositionSnapshots.add(positionSnapshot);
    }
}
