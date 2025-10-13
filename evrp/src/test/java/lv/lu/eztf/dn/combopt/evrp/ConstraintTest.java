package lv.lu.eztf.dn.combopt.evrp;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import lv.lu.eztf.dn.combopt.evrp.domain.Customer;
import lv.lu.eztf.dn.combopt.evrp.domain.EVRPsolution;
import lv.lu.eztf.dn.combopt.evrp.domain.Location;
import lv.lu.eztf.dn.combopt.evrp.domain.Vehicle;
import lv.lu.eztf.dn.combopt.evrp.solver.ConstraintStreamCostFunction;
import org.junit.jupiter.api.Test;

public class ConstraintTest {
    private ConstraintVerifier<ConstraintStreamCostFunction, EVRPsolution> constraintVerifier =
            ConstraintVerifier.build(new ConstraintStreamCostFunction(), EVRPsolution.class, Vehicle.class);
    @Test
    void testTotalDistance() {
        Vehicle vehicle = new Vehicle();
        Location depot = new Location(0l, 0.0, 0.0);
        vehicle.setDepot(depot);
        Location loc = new Location(1l, 0.0, 1.0);
        Customer customer = new Customer();
        customer.setLocation(loc);
        vehicle.getVisits().add(customer);

        constraintVerifier.verifyThat(ConstraintStreamCostFunction::totalDistance)
                .given(vehicle, customer, depot, loc)
                .penalizesBy(2000);
    }
}
