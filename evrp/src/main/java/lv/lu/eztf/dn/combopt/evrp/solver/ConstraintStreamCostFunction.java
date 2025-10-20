package lv.lu.eztf.dn.combopt.evrp.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.eztf.dn.combopt.evrp.domain.Vehicle;
import lv.lu.eztf.dn.combopt.evrp.domain.Visit;
import org.jspecify.annotations.NonNull;

public class ConstraintStreamCostFunction implements ConstraintProvider {
    @Override
    public Constraint @NonNull [] defineConstraints(@NonNull ConstraintFactory constraintFactory) {
        return new Constraint[] {
                //penalizeEveryVisit(constraintFactory),
                totalDistance(constraintFactory),
                batteryEmpty(constraintFactory),
                visitTimeWindowViolated(constraintFactory),
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
}
