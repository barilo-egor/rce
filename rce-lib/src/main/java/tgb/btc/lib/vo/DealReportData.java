package tgb.btc.lib.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DealReportData {

    private LocalDate firstDay;

    private LocalDate lastDay;

    private String period;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDate getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(LocalDate firstDay) {
        this.firstDay = firstDay;
    }

    public LocalDate getLastDay() {
        return lastDay;
    }

    public void setLastDay(LocalDate lastDay) {
        this.lastDay = lastDay;
    }

}
