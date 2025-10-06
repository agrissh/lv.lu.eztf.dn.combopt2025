package lv.lu.eztf.dn.combopt.evrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public abstract class  Visit {

    Location location;
    Long startTime; // second of a day
    Long endTime; // second of a day
    String name;

    public abstract Long getVisitTime();
    public Long getArrivalTime() {
        // get previous departure time
        // get time needed to get from previous to this
        // TODO: implement this
        return 0l;
    }

    public Long getDepartureTime() {
        return Math.max(this.getArrivalTime(), this.getStartTime()) + this.getVisitTime();
    }

}
