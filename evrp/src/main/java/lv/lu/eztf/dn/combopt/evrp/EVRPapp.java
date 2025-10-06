package lv.lu.eztf.dn.combopt.evrp;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import lombok.extern.slf4j.Slf4j;
import lv.lu.eztf.dn.combopt.evrp.domain.*;
import lv.lu.eztf.dn.combopt.evrp.solver.EasyCostFunction;
import lv.lu.eztf.dn.combopt.evrp.solver.EasyJustDistanceCostFunction;

import java.time.Duration;
import java.util.List;

@Slf4j
public class EVRPapp {
    public static void main(String[] args) {
        EVRPsolution problem = createExample();

        // Run optimizer
        SolverFactory<EVRPsolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(EVRPsolution.class)
                        .withEntityClasses(Vehicle.class)
                        .withEasyScoreCalculatorClass(EasyCostFunction.class)
                        .withTerminationSpentLimit(Duration.ofSeconds(5)));
        Solver<EVRPsolution> solver = solverFactory.buildSolver();
        EVRPsolution solution = solver.solve(problem);

        printExample(solution);
    }

    private static EVRPsolution createExample() {
        EVRPsolution problem = new EVRPsolution();
        problem.setName("TEST EVRP problem");

        Vehicle vehicle = new Vehicle();
        vehicle.setRegNr("AA-1111");

        Location depot = new Location(0l, 0.0, 0.0);
        vehicle.setDepot(depot);

        vehicle.setCharge(7.0);
        vehicle.setCostHourly(7.0);
        vehicle.setCostUsage(30.0);
        vehicle.setDischargeSpeed(1.0);
        vehicle.setMaxCharge(12.0);
        vehicle.setMaxChargePower(2.0);
        vehicle.setOperationStartingTime(0l);
        vehicle.setOperationEndingTime(3600 * 8l);
        vehicle.setPriceEnergyDepot(1.0);
        vehicle.setServiceDurationAtFinish(60 * 10l);
        vehicle.setServiceDurationAtStart(60 * 5l);

        Customer customer1 = new Customer();
        customer1.setName("Customer 1");
        Location loc1 = new Location(1l, 4.0, 0.0);
        customer1.setLocation(loc1);
        customer1.setServiceDuration(60 * 15l);
        customer1.setStartTime(0l);
        customer1.setEndTime(3600 * 8l);

        Customer customer2 = new Customer();
        customer2.setName("Customer 2");
        Location loc2 = new Location(2l, 4.0, 3.0);
        customer2.setLocation(loc2);
        customer2.setServiceDuration(60 * 15l);
        customer2.setStartTime(0l);
        customer2.setEndTime(3600 * 8l);

        Customer customer3 = new Customer();
        customer3.setName("Customer 3");
        Location loc3 = new Location(3l, 0.0, 3.0);
        customer3.setLocation(loc3);
        customer3.setServiceDuration(60 * 15l);
        customer3.setStartTime(0l);
        customer3.setEndTime(3600 * 8l);

        ChargingStation chargingStation = new ChargingStation();
        chargingStation.setName("Charging Station");
        Location locCS = new Location(4l, 3.0, 2.0);
        chargingStation.setLocation(locCS);
        chargingStation.setStartTime(0l);
        chargingStation.setEndTime(3600 * 8l);
        chargingStation.setChargingPower(3.0);
        chargingStation.setPriceEnergy(1.5);
        chargingStation.setNumberOfSlots(2);

        problem.getVisitList().addAll(List.of(customer1, customer2, customer3, chargingStation));
        problem.getLocationList().addAll(List.of(depot, loc1, loc2, loc3, locCS));
        problem.getVehicleList().add(vehicle);

        return problem;
    }

    private static void printExample(EVRPsolution solution) {
        log.info("Printing EVRP solution %s with score %s."
                .formatted(solution.getName(), solution.getScore().toString()));
        for (Vehicle vehicle : solution.getVehicleList()) {
            Double charge = vehicle.getCharge();
            log.info("Vehicle %s with charge %f:"
                    .formatted(vehicle.getRegNr(), vehicle.getCharge()));
            Location prevLoc = vehicle.getDepot();
            for (Visit visit : vehicle.getVisits()) {
                charge = charge - vehicle.getDischargeSpeed() * prevLoc.distanceTo(visit.getLocation());
                log.info("Visited %s %s located in (%f, %f), remaining charge %f"
                        .formatted(
                                visit instanceof Customer ? "customer" : "charging station",
                                visit.getName(),
                                visit.getLocation().getLon(), visit.getLocation().getLat(),
                                charge));
                if (visit instanceof ChargingStation) {
                    charge = vehicle.getMaxCharge();
                    log.info("Charged! Full charge %f"
                            .formatted(charge));
                }
                prevLoc = visit.getLocation();
            }
            charge = charge - vehicle.getDischargeSpeed() * prevLoc.distanceTo(vehicle.getDepot());
            log.info("Charge when finished in depot %f"
                    .formatted(charge));
        }
        log.info("===================================================");

    }

}
