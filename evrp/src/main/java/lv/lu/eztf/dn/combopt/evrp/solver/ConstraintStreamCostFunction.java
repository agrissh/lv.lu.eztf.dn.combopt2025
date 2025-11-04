package lv.lu.eztf.dn.combopt.evrp.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.eztf.dn.combopt.evrp.domain.ChargingStation;
import lv.lu.eztf.dn.combopt.evrp.domain.Vehicle;
import lv.lu.eztf.dn.combopt.evrp.domain.Visit;
import org.jspecify.annotations.NonNull;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class ConstraintStreamCostFunction implements ConstraintProvider {
    @Override
    public Constraint @NonNull [] defineConstraints(@NonNull ConstraintFactory constraintFactory) {
        return new Constraint[] {
                //penalizeEveryVisit(constraintFactory),
                //totalDistance(constraintFactory),
                //batteryEmpty(constraintFactory),
                visitTimeWindowViolated(constraintFactory),
                visitChargeNegative(constraintFactory),
                depotChargeNegative(constraintFactory),
                depotTimeWindowViolated(constraintFactory),
                costVehicleUsage(constraintFactory),
                costVehicleTime(constraintFactory),
                costInitialEnergy(constraintFactory),
                costRechargedEnergy(constraintFactory),
                rewardLeftover(constraintFactory)
        };
    }
    public Constraint penalizeEveryVisit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Every Visit");
    }

    public Constraint totalDistance(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getTotalDistance() * 1000))
                .asConstraint("Total distance for a vehicle");
    }

    // This actually is BAD constraint braking incremental score calculation
    public Constraint batteryEmpty(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> vehicle.isBatteryEmpty())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Battery empty");
    }

    // This actually is BAD constraint braking incremental score calculation
    public Constraint visitTimeWindowViolated(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() > visit.getEndTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("TW violation");
    }

    public Constraint visitChargeNegative(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVehicleCharge() < 0)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Visit with empty battery");
    }

    public Constraint depotChargeNegative(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .filter((visit, vehicle) -> visit.getVehicleChargeAfterVisit() -
                        vehicle.getDischargeSpeed() * visit.getLocation().distanceTo(vehicle.getDepot()) < 0 )
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Depot with empty battery");
    }

    public Constraint depotTimeWindowViolated(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .filter((visit, vehicle) -> visit.getDepartureTime() +
                        visit.getLocation().timeTo(vehicle.getDepot()) + vehicle.getServiceDurationAtFinish() >
                        vehicle.getOperationEndingTime())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Depot TW violation");
    }

    public Constraint costVehicleUsage(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getCostUsage() * 100))
                .asConstraint("Vehicle usage cost");
    }

    public Constraint costVehicleTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) -> (int) Math.round(
                        (visit.getDepartureTime() + visit.getLocation().timeTo(vehicle.getDepot()) +
                        vehicle.getServiceDurationAtFinish() - vehicle.getOperationStartingTime())
                        * vehicle.getCostHourly() * 100 / 3600))
                .asConstraint("Vehicle hourly cost");
    }

    public Constraint costInitialEnergy(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getPriceEnergyDepot() *
                        100 * vehicle.getCharge()))
                .asConstraint("Initial energy cost");
    }

    public Constraint costRechargedEnergy(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ChargingStation.class)
                .penalize(HardSoftScore.ONE_SOFT, station -> (int) Math.round(station.getPriceEnergy() *
                        100 * (station.getVehicleChargeAfterVisit() - station.getVehicleCharge())))
                .asConstraint("Recharged energy cost");
    }

    public Constraint rewardLeftover(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ChargingStation.class)
                .groupBy(ChargingStation::getVehicle, ConstraintCollectors.max(ChargingStation::getPriceEnergy))
                // TODO: expand with cars without charging stations!
                .join(Visit.class, equal((v,p)->v, Visit::getVehicle))
                .filter((vehicle, price, visit) -> visit.getNext() == null)
                .reward(HardSoftScore.ONE_SOFT,(vehicle, maxStationPrice, lastVisit) -> (int) Math.round(Math.max(vehicle.getPriceEnergyDepot(), maxStationPrice) * 100 *
                                Math.max(0, lastVisit.getVehicleChargeAfterVisit() - lastVisit.getLocation().distanceTo(vehicle.getDepot()) * vehicle.getDischargeSpeed())))
                .asConstraint("Leftover reward");
    }
}

