package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.ShadowSources;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity @Slf4j
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(scope = Vehicle.class, property = "regNr",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Vehicle {
    String regNr;
    @JsonIdentityReference(alwaysAsId = true)
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
    @JsonIdentityReference(alwaysAsId = true)
    Visit last = null;
    // TODO: THIS DOES NOT WORK !!!!!
    @ShadowSources("visits")
    @JsonIgnore
    public Visit lastSupplier() {
        Visit last = null;
        if (!this.getVisits().isEmpty()) {
            last = this.getVisits().get(this.getVisits().size() - 1);
        }
        //log.info(String.valueOf(last));
        return last;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    @JsonIgnore
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
