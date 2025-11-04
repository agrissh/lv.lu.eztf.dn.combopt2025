package lv.lu.eztf.dn.combopt.evrp.domain;

import ai.timefold.solver.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class JsonIO extends JacksonSolutionFileIO<EVRPsolution> {
    public JsonIO() {
        super(EVRPsolution.class);
    }
}
