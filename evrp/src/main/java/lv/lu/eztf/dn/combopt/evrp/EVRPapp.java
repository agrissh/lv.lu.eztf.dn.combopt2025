package lv.lu.eztf.dn.combopt.evrp;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.*;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.SolverManagerConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lombok.extern.slf4j.Slf4j;
import lv.lu.eztf.dn.combopt.evrp.domain.*;
import lv.lu.eztf.dn.combopt.evrp.solver.ConstraintStreamCostFunction;
import lv.lu.eztf.dn.combopt.evrp.solver.EasyCostFunction;
import lv.lu.eztf.dn.combopt.evrp.solver.EasyJustDistanceCostFunction;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EVRPapp {
    public static void main(String[] args) {
        EVRPsolution problem = createExample();

        // Run optimizer
        SolverConfig solverConfigFromXML = SolverConfig.createFromXmlResource("SolverConfig.xml");
        //solverConfigFromXML.withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(10l));
        SolverFactory<EVRPsolution> solverFactoryFromConfigXML = SolverFactory.create(solverConfigFromXML);
        //SolverFactory<EVRPsolution> solverFactoryFromConfigXML = SolverFactory.createFromXmlResource("SolverConfig.xml");

        SolverFactory<EVRPsolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(EVRPsolution.class)
                        .withEntityClasses(Vehicle.class, Visit.class)
                        //.withEasyScoreCalculatorClass(EasyJustDistanceCostFunction.class)
                        .withConstraintProviderClass(ConstraintStreamCostFunction.class)
                        .withTerminationSpentLimit(Duration.ofSeconds(5))
                        .withEnvironmentMode(EnvironmentMode.PHASE_ASSERT));

        SolverManager<EVRPsolution, String> solverManager = SolverManager.create(solverConfigFromXML, new SolverManagerConfig());
        String problemId = UUID.randomUUID().toString();
        //SolverJob<EVRPsolution, String> solverJob = solverManager.solve(problemId, problem);
        //SolverJob<EVRPsolution, String> solverJob = solverManager.solveAndListen(problemId, problem, EVRPapp::printExample);

        //try {
        //    EVRPsolution job_solution = solverJob.getFinalBestSolution();
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //} catch (ExecutionException e) {
        //    throw new RuntimeException(e);
        //}
        solverManager.solveBuilder()
                .withProblemId(problemId)
                .withProblem(problem)
                .withBestSolutionConsumer(EVRPapp::printExample)
                .run();
        solverManager.solveBuilder()
                .withProblemId(UUID.randomUUID().toString())
                .withProblem(problem)
                .run();

        Solver<EVRPsolution> solver = solverFactoryFromConfigXML.buildSolver();
        EVRPsolution solution = solver.solve(problem);

        printExample(solution);

        SolutionManager<EVRPsolution, HardSoftScore> solutionManager = SolutionManager.create(solverFactoryFromConfigXML);
        log.info(solutionManager.explain(solution).getSummary());

    }

    private static EVRPsolution createExample() {
        EVRPsolution problem = new EVRPsolution();
        problem.setName("TEST EVRP problem");

        Vehicle vehicle = new Vehicle();
        vehicle.setRegNr("AA-1111");
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRegNr("BB-1111");

        Location depot = new Location(0l, 0.0, 0.0);
        vehicle.setDepot(depot);
        vehicle2.setDepot(depot);

        vehicle.setCharge(7.0);
        vehicle.setCostHourly(7.0);
        vehicle.setCostUsage(30.0);
        vehicle.setDischargeSpeed(1.0);
        vehicle.setMaxCharge(12.0);
        vehicle.setMaxChargePower(2.0);
        vehicle.setOperationStartingTime(0l);
        vehicle.setOperationEndingTime(3600 * 6l);
        vehicle.setPriceEnergyDepot(1.0);
        vehicle.setServiceDurationAtFinish(60 * 10l);
        vehicle.setServiceDurationAtStart(60 * 5l);

        vehicle2.setCharge(4.0);
        vehicle2.setCostHourly(7.0);
        vehicle2.setCostUsage(30.0);
        vehicle2.setDischargeSpeed(1.0);
        vehicle2.setMaxCharge(12.0);
        vehicle2.setMaxChargePower(2.0);
        vehicle2.setOperationStartingTime(0l);
        vehicle2.setOperationEndingTime(3600 * 6l);
        vehicle2.setPriceEnergyDepot(1.0);
        vehicle2.setServiceDurationAtFinish(60 * 10l);
        vehicle2.setServiceDurationAtStart(60 * 5l);

        Customer customer1 = new Customer();
        customer1.setName("Customer RIGHT BOTTTOM");
        Location loc1 = new Location(1l, 4.0, 0.0);
        customer1.setLocation(loc1);
        customer1.setServiceDuration(60 * 15l);
        customer1.setStartTime(0l);
        customer1.setEndTime(3600 * 8l);

        Customer customer2 = new Customer();
        customer2.setName("Customer RIGHT UPPER");
        Location loc2 = new Location(2l, 4.0, 3.0);
        customer2.setLocation(loc2);
        customer2.setServiceDuration(60 * 15l);
        customer2.setStartTime(0l);
        customer2.setEndTime(3600 * 8l);

        Customer customer3 = new Customer();
        customer3.setName("Customer LEFT UPPER");
        Location loc3 = new Location(3l, 0.0, 3.0);
        customer3.setLocation(loc3);
        customer3.setServiceDuration(60 * 15l);
        customer3.setStartTime(0l);
        customer3.setEndTime(3600 * 8l);

        Customer customer4 = new Customer();
        customer4.setName("Customer FAR AWAY");
        Location loc4 = new Location(3l, 6.0, 6.0);
        customer4.setLocation(loc4);
        customer4.setServiceDuration(60 * 15l);
        customer4.setStartTime(0l);
        customer4.setEndTime(3600 * 8l);

        ChargingStation chargingStation = new ChargingStation();
        chargingStation.setName("Charging Station");
        Location locCS = new Location(  4l, 3.0, 2.0);
        chargingStation.setLocation(locCS);
        chargingStation.setStartTime(0l);
        chargingStation.setEndTime(3600 * 8l);
        chargingStation.setChargingPower(3.0);
        chargingStation.setPriceEnergy(1.5);
        chargingStation.setNumberOfSlots(2);

        ChargingStation chargingStation2 = new ChargingStation();
        chargingStation2.setName("Charging Station 2");
        chargingStation2.setLocation(locCS);
        chargingStation2.setStartTime(0l);
        chargingStation2.setEndTime(3600 * 8l);
        chargingStation2.setChargingPower(3.0);
        chargingStation2.setPriceEnergy(1.5);
        chargingStation2.setNumberOfSlots(2);


        problem.getVisitList().addAll(List.of(customer1, customer2, customer3, chargingStation, customer4, chargingStation2));
        problem.getLocationList().addAll(List.of(depot, loc1, loc2, loc3, locCS, loc4));
        problem.getVehicleList().addAll(List.of(vehicle, vehicle2));

        return problem;
    }

    private static void printExample(EVRPsolution solution) {
        log.info("Printing EVRP solution %s with score %s."
                .formatted(solution.getName(), solution.getScore().toString()));
        for (Vehicle vehicle : solution.getVehicleList()) {
            log.info("Vehicle %s with charge %.2f departing at %d :"
                    .formatted(vehicle.getRegNr(), vehicle.getCharge(),
                            vehicle.getOperationStartingTime()  + vehicle.getServiceDurationAtStart()));
            for (Visit visit : vehicle.getVisits()) {
                log.info("Visited %s %s located in (%.2f, %.2f), remaining charge %.2f, arrived at %d, departure at %d"
                        .formatted(
                                visit instanceof Customer ? "customer" : "charging station",
                                visit.getName(),
                                visit.getLocation().getLon(), visit.getLocation().getLat(),
                                visit.getVehicleCharge(),
                                visit.getArrivalTime(),
                                visit.getDepartureTime()));
                if (visit instanceof ChargingStation) {
                    log.info("Charged! Full charge %.2f"
                            .formatted(visit.getVehicleChargeAfterVisit()));
                }
            }
            log.info("Charge when finished in depot %.2f"
                    .formatted(vehicle.getLast() != null ? vehicle.getLast().getVehicleChargeAfterVisit() - vehicle.getDischargeSpeed() *
                            vehicle.getLast().getLocation().distanceTo(vehicle.getDepot()) :
                            vehicle.getCharge()));
        }
        log.info("===================================================");

    }

}
