package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;

import java.util.ArrayList;
import java.util.List;

public class PowerProfileSnapshot extends AbstractSnapshot {

    private PowerProfileDetail detail = new PowerProfileDetail();

    private EntityId settledPriceIndexId;

    private List<PowerProfileDaySnapshot> profileDays = new ArrayList<PowerProfileDaySnapshot>();

    private List<PowerProfileIndexMappingSnapshot> indexMappings = new ArrayList<>();

    public PowerProfileDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfileDetail detail) {
        this.detail = detail;
    }

    public List<PowerProfileDaySnapshot> getProfileDays() {
        return profileDays;
    }

    public void setProfileDays(List<PowerProfileDaySnapshot> profileDays) {
        this.profileDays = profileDays;
    }

    public void addPowerProfileDay(PowerProfileDaySnapshot day) {
        this.profileDays.add(day);
    }

    public PowerProfileDaySnapshot getPowerProfileDayByDayOfWeek(int dayOfWeek) {
        return profileDays.stream().filter(c -> c.getDetail().getDayOfWeek() == dayOfWeek).findFirst().orElse(null);
    }

    public EntityId getSettledPriceIndexId() {
        return settledPriceIndexId;
    }

    public void setSettledPriceIndexId(EntityId settledPriceIndexId) {
        this.settledPriceIndexId = settledPriceIndexId;
    }

    public List<PowerProfileIndexMappingSnapshot> getIndexMappings() {
        return indexMappings;
    }

    public void setIndexMappings(List<PowerProfileIndexMappingSnapshot> indexMappings) {
        this.indexMappings = indexMappings;
    }

    public EntityId findPriceIndexMappingByFlowCode(PowerFlowCode code) {
        return indexMappings
                .stream()
                .filter(c-> c.getDetail().getPowerFlowCode() == code)
                .map(d -> d.getPriceIndexId())
                .findFirst().orElse(null);
    }
}
