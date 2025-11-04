package lv.lu.eztf.dn.combopt.evrp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
                           Vehicle vehicle,
                           Visit previous,
                           Visit next,
                           Long arrivalTime,
                           Double vehicleCharge,
                           Double chargingPower,
                           Integer numberOfSlots,
                           Double priceEnergy)  {
        super(location, startTime, endTime, name, vehicle, previous, next, arrivalTime, vehicleCharge);
        this.chargingPower = chargingPower;
        this.numberOfSlots = numberOfSlots;
        this.priceEnergy = priceEnergy;
    }

    @Override
    public Long getVisitTime() {
        Vehicle car = this.getVehicle();
        // TODO: wait time for a free slot
        // calculate charging time
        return car != null ?
                (long) (((car.getMaxCharge() - car.getCharge()) / Math.min(this.chargingPower, car.getMaxChargePower())) * 3600)
                : null;
    }

    @Override
    public Double getVehicleChargeAfterVisit() {
        return this.getVehicle() != null ? this.getVehicle().getMaxCharge() : null;
    }
}
