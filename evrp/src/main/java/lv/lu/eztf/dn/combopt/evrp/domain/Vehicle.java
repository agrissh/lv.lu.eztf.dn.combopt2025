package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowSources;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Vehicle {
    String regNr;
    Location depot;
    Long serviceDurationAtStart; // sec
    Long serviceDurationAtFinish; // sec
    Long operationStartingTime; // second of a day
    Long operationEndingTime; // second of a day
    Double maxCharge; // kWh
    Double charge; // kWh
    Double dischargeSpeed; // kWh / km
    Double maxChargePower; // kWh / hour
    Double costUsage; // euro
    Double costHourly; // euro / hour
    Double priceEnergyDepot; // euro / KWh
    @PlanningListVariable
    List<Visit> visits = new ArrayList<>();
    @ShadowVariable(supplierName = "lastSupplier")
    Visit last = null;
    @ShadowSources("visits")
    public Visit lastSupplier() {
        if (this.getVisits().isEmpty()) {
            return null;
        } else {
            return this.getVisits().get(this.getVisits().size() - 1);
        }
    }

    public Double getTotalDistance() {
        Double totalDistance = 0.0;
        Location prevLoc = this.getDepot();
        for (Visit visit: this.getVisits()) {
            totalDistance = totalDistance +
                    prevLoc.distanceTo(visit.getLocation());
            prevLoc = visit.getLocation();
        }
        totalDistance = totalDistance +
                prevLoc.distanceTo(this.getDepot());
        return totalDistance;
    }

    public Boolean isBatteryEmpty() {
        Boolean batteryEmpty = false;
        Double charge = this.getCharge();
        Location prevLoc = this.getDepot();
        for (Visit visit: this.getVisits()) {
            charge = charge - this.getDischargeSpeed() * prevLoc.distanceTo(visit.getLocation());
            if (charge < 0) { batteryEmpty = true; }
            if (visit instanceof ChargingStation) {charge = this.getMaxCharge(); }
            prevLoc = visit.getLocation();
        }
        charge = charge - this.getDischargeSpeed() * prevLoc.distanceTo(this.getDepot());
        if (charge < 0) { batteryEmpty = true; }
        return batteryEmpty;

    }

    @Override
    public String toString() {
        return this.regNr;
    }
}
