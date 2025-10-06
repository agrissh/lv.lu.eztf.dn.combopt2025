package lv.lu.eztf.dn.combopt.evrp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ChargingStation extends Visit {
    Double chargingPower; // kWh / hour
    Integer numberOfSlots;
    Double priceEnergy; // euro / KWh

    public ChargingStation(Location location,
                           Long startTime,
                           Long endTime,
                           String name,
                           Double chargingPower,
                           Integer numberOfSlots,
                           Double priceEnergy)  {
        super(location, startTime, endTime, name);
        this.chargingPower = chargingPower;
        this.numberOfSlots = numberOfSlots;
        this.priceEnergy = priceEnergy;
    }

    @Override
    public Long getVisitTime() {
        // TODO: implement this
        // TODO: we have to know what car is used!!!
        Vehicle car = new Vehicle();
        // TODO: wait time for a free slot
        // calculate charging time
        return (long) (((car.getMaxCharge() - car.getCharge()) / Math.min(this.chargingPower, car.getMaxChargePower())) * 3600);
    }
}
