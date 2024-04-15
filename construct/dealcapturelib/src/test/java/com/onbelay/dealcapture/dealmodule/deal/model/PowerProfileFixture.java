package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;

import java.time.DayOfWeek;

public class PowerProfileFixture {



    public static PowerProfileSnapshot createPowerProfileSnapshotAllDaysAllHours(String name) {
        PowerProfileSnapshot snapshot = new PowerProfileSnapshot();
        snapshot.getDetail().setName(name);

        for (int i=1; i < 8; i++) {
            PowerProfileDaySnapshot day = new PowerProfileDaySnapshot();
            DayOfWeek dayOfWeek = DayOfWeek.of(i);
            if (dayOfWeek == DayOfWeek.SATURDAY  || dayOfWeek == DayOfWeek.SUNDAY) {
                setAllHours(day, PowerFlowCode.OFF_PEAK);
            } else {
                setAllHours(day, PowerFlowCode.ON_PEAK);
            }
            snapshot.addPowerProfileDay(day);
        }

        return snapshot;
    }


    public static void setAllHours(PowerProfileDaySnapshot snapshot, PowerFlowCode flowCode) {
        for (int i=1; i < 25; i++) {
            snapshot.getDetail().setPowerFlowCode(i, flowCode);
        }


    }
}
