package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealOverride {
    private List<String> headers;
    private String ticketNo;
    private LocalDate overrideDate;
    private Integer hourEnding;
    private List<BigDecimal> values;

    public DealOverride(List<String> headers) {
        this.headers = headers;
        values = new ArrayList<>(headers.size());
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public LocalDate getMonthDate() {
        return overrideDate.withDayOfMonth(1);
    }

    public LocalDate getOverrideDate() {
        return overrideDate;
    }

    public void setOverrideDate(LocalDate overrideDate) {
        this.overrideDate = overrideDate;
    }

    public Integer getHourEnding() {
        return hourEnding;
    }

    public void setHourEnding(Integer hourEnding) {
        this.hourEnding = hourEnding;
    }

    public BigDecimal getValueAt(int index) {
        return values.get(index);
    }

    public void addValue(BigDecimal value) {
        values.add(value);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<BigDecimal> getValues() {
        return values;
    }
}
