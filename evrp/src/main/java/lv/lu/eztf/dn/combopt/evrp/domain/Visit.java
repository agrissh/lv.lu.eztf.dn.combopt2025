package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
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
    @CascadingUpdateShadowVariable(targetMethodName = "updateShadows")
    public Long arrivalTime = null;
    @CascadingUpdateShadowVariable(targetMethodName = "updateShadows")
    public Double vehicleCharge = null;
    public void updateShadows() {
        if (this.getVehicle() == null) {
            this.setArrivalTime(null);
            this.setVehicleCharge(null);
        } else {
            this.setArrivalTime(
                    this.getPrevious() == null ?
                            this.getVehicle().getOperationStartingTime() +
                            this.getVehicle().getServiceDurationAtStart() +
                            this.getVehicle().getDepot().timeTo(this.getLocation()) :
                            this.getPrevious().getDepartureTime() +
                            this.getPrevious().getLocation().timeTo(this.getLocation())
            );
            this.setVehicleCharge(
                    this.getPrevious() == null ?
                            this.getVehicle().getCharge() - this.getVehicle().getDischargeSpeed() *
                            this.getVehicle().getDepot().distanceTo(this.getLocation()) :
                            this.getPrevious().getVehicleChargeAfterVisit() - this.getVehicle().getDischargeSpeed() *
                            this.getPrevious().getLocation().distanceTo(this.getLocation())
            );
        }
    }
    public abstract Double getVehicleChargeAfterVisit();
    public Long getArrivalTime_recursive() {
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
