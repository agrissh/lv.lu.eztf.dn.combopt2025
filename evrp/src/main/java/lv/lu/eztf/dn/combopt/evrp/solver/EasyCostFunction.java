package lv.lu.eztf.dn.combopt.evrp.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import lv.lu.eztf.dn.combopt.evrp.domain.*;
import org.jspecify.annotations.NonNull;

public class EasyCostFunction implements EasyScoreCalculator<EVRPsolution, HardSoftScore> {
    @Override
    public @NonNull HardSoftScore calculateScore(@NonNull EVRPsolution evrPsolution) {
        int hard = 0, soft = 0;

        Double totalDistance = 0.0;
        for (Vehicle v : evrPsolution.getVehicleList()) {
            totalDistance += v.getTotalDistance();
        }
        soft = (int) (totalDistance * 1000);

        for (Vehicle vehicle : evrPsolution.getVehicleList()) {
            Double charge = vehicle.getCharge();
            Location prevLoc = vehicle.getDepot();
            for (Visit visit: vehicle.getVisits()) {
                charge = charge - vehicle.getDischargeSpeed() * prevLoc.distanceTo(visit.getLocation());
                if (charge < 0) { hard = (int) (hard - (charge * 100)); }
                if (visit instanceof ChargingStation) {charge = vehicle.getMaxCharge(); }
                prevLoc = visit.getLocation();
            }
            charge = charge - vehicle.getDischargeSpeed() * prevLoc.distanceTo(vehicle.getDepot());
            if (charge < 0) { hard = (int) (hard - (charge * 100)); }
        }

        return HardSoftScore.of(- hard, - soft);
    }
}
