package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerProfileSnapshot extends AbstractSnapshot {

    private PowerProfileDetail detail = new PowerProfileDetail();

    private EntityId settledPriceIndexId;

    private EntityId endOfMonthPriceIndexId;

    private List<PowerProfileDaySnapshot> profileDays = new ArrayList<PowerProfileDaySnapshot>();

    private Map<Integer, PowerProfileDaySnapshot> daysMap = new HashMap<>();

    private List<PowerProfileIndexMappingSnapshot> indexMappings = new ArrayList<>();

    public PowerProfileSnapshot() {
    }

    public PowerProfileSnapshot(EntityId entityId) {
        super(entityId);
    }

    public PowerProfileSnapshot(String errorCode) {
        super(errorCode);
    }

    public PowerProfileSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public PowerProfileSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

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

    public EntityId getEndOfMonthPriceIndexId() {
        return endOfMonthPriceIndexId;
    }

    public void setEndOfMonthPriceIndexId(EntityId endOfMonthPriceIndexId) {
        this.endOfMonthPriceIndexId = endOfMonthPriceIndexId;
    }

    public Map<Integer, PowerProfileDaySnapshot> getDaysMap() {
        return daysMap;
    }

    public void setDaysMap(Map<Integer, PowerProfileDaySnapshot> daysMap) {
        this.daysMap = daysMap;
    }

    public EntityId findPriceIndexMappingByFlowCode(PowerFlowCode code) {
        return indexMappings
                .stream()
                .filter(c-> c.getDetail().getPowerFlowCode() == code)
                .map(d -> d.getPriceIndexId())
                .findFirst().orElse(null);
    }

    public void addIndexMappingSnapshot(PowerProfileIndexMappingSnapshot indexMappingSnapshot) {
        indexMappings.add(indexMappingSnapshot);
    }
}
