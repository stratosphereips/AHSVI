package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;

import java.util.logging.Logger;

public class HSVIInSolveMethod extends InSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public HSVIInSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        super.callMethod();
        solveMethodsContainer.setNewLbVal(hsvi.getLBValueInInitBelief());
        solveMethodsContainer.setNewUbVal(hsvi.getUBValueInInitBelief());

        LOGGER.fine("LB in init belief: " + solveMethodsContainer.getLbVal());
        LOGGER.fine(String.format(" ----- Diff to last iteration: %.20f\n",
                (solveMethodsContainer.getLbVal() - solveMethodsContainer.getLastLbVal())));
        LOGGER.finer("LB size: " + hsvi.getLbFunction().getAlphaVectors().size());
        LOGGER.fine("UB in init belief: " + solveMethodsContainer.getUbVal());
        LOGGER.fine(String.format(" ----- Diff to last iteration: %.20f\n",
                (solveMethodsContainer.getUbVal() - solveMethodsContainer.getLastUbVal())));
        LOGGER.finer("UB size: " + hsvi.getUbFunction().getPoints().size());
    }
}
