package lv.lu.eztf.dn.combopt.evrp.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter @Setter @NoArgsConstructor
public class ChargingStation extends Visit {
    Double chargingPower; // kWh / hour
    Integer numberOfSlots;
    Double priceEnergy; // euro / KWh
    @JsonIdentityReference(alwaysAsId = true)
    MultiSlotCS parent;
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
                           Double priceEnergy,
                           MultiSlotCS parent)  {
        super(location, startTime, endTime, name, vehicle, previous, next, arrivalTime, vehicleCharge);
        this.chargingPower = chargingPower;
        this.numberOfSlots = numberOfSlots;
        this.priceEnergy = priceEnergy;
        this.parent = parent;
    }

    @Override
    public Long getVisitTime() {
        Vehicle car = this.getVehicle();
        // TODO: wait time for a free slot
        // calculate charging time
        //log.info(this.getVehicleCharge().toString());
        return car != null && this.getVehicleCharge() != null ?
                (long) (((car.getMaxCharge() - this.getVehicleCharge()) / Math.min(this.chargingPower, car.getMaxChargePower())) * 3600)
                : null;
    }

    @Override
    public Double getVehicleChargeAfterVisit() {
        return this.getVehicle() != null ? this.getVehicle().getMaxCharge() : null;
    }
}
