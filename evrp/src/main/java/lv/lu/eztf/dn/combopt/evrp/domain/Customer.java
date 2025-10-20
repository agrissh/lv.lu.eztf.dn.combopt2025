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
                    Vehicle vehicle,
                    Visit previous,
                    Visit next,
                    Long serviceDuration) {
        super(location, startTime, endTime, name, vehicle, previous, next);
        this.serviceDuration = serviceDuration;
    }

    @Override
    public Long getVisitTime() {
        return this.getServiceDuration();
    }
}
