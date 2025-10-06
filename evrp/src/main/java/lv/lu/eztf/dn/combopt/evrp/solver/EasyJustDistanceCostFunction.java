package lv.lu.eztf.dn.combopt.evrp.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import lv.lu.eztf.dn.combopt.evrp.domain.EVRPsolution;
import lv.lu.eztf.dn.combopt.evrp.domain.Vehicle;
import org.jspecify.annotations.NonNull;

public class EasyJustDistanceCostFunction implements EasyScoreCalculator<EVRPsolution, HardSoftScore> {
    @Override
    public @NonNull HardSoftScore calculateScore(@NonNull EVRPsolution evrPsolution) {
        int hard = 0, soft = 0;

        Double totalDistance = 0.0;
        for (Vehicle v : evrPsolution.getVehicleList()) {
            totalDistance += v.getTotalDistance();
        }
        soft = (int) (totalDistance * 1000);
        return HardSoftScore.of(- hard, - soft);
    }
}
