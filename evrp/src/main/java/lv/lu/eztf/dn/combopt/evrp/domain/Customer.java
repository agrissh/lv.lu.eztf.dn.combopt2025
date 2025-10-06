package lv.lu.eztf.dn.combopt.evrp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Customer extends Visit {
    Long serviceDuration; // seconds

    public Customer(Location location,
                    Long startTime,
                    Long endTime,
                    String name,
                    Long serviceDuration) {
        super(location, startTime, endTime, name);
        this.serviceDuration = serviceDuration;
    }

    @Override
    public Long getVisitTime() {
        return this.getServiceDuration();
    }
}
