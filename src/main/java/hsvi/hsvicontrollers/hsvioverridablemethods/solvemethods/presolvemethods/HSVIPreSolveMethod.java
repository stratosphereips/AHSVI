package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.CustomLogger.CustomLogger;

import java.util.logging.Logger;

public class HSVIPreSolveMethod extends PreSolveMethod{

    private static final Logger LOGGER = CustomLogger.getLogger();

    public HSVIPreSolveMethod() {
        super();
    }

    @Override
    public void overridableMethod() {
        solveMethodsContainer.setLbVal(hsvi.getLBValueInInitBelief());
        solveMethodsContainer.setUbVal(hsvi.getUBValueInInitBelief());
        LOGGER.fine("LB in init belief: " + solveMethodsContainer.getLbVal());
        LOGGER.fine("UB in init belief: " + solveMethodsContainer.getUbVal());
    }
}
