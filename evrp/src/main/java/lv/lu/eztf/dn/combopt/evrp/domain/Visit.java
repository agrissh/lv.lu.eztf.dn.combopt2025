package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.NextElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningEntity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public abstract class  Visit {

    Location location;
    Long startTime; // second of a day
    Long endTime; // second of a day
    String name;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    Vehicle vehicle;
    @PreviousElementShadowVariable(sourceVariableName = "visits")
    Visit previous;
    @NextElementShadowVariable(sourceVariableName = "visits")
    Visit next;
    public abstract Long getVisitTime();
    public Long getArrivalTime() {
        Long prevDepartureTime;
        Location prevLocation;
        if (this.getPrevious()==null && this.getVehicle()!=null) {
            prevDepartureTime = this.getVehicle().getOperationStartingTime() +
                    this.getVehicle().getServiceDurationAtStart();
            prevLocation = this.getVehicle().getDepot();
        } else {
            prevDepartureTime = this.getPrevious().getDepartureTime();
            prevLocation = this.getPrevious().getLocation();
        }

        return prevDepartureTime + prevLocation.timeTo(this.getLocation());
    }
    public Long getDepartureTime() {
        return Math.max(this.getArrivalTime(), this.getStartTime()) + this.getVisitTime();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
