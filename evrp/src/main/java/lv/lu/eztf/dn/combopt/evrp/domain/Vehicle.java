package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
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
}
