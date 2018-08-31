package hsvi.hsvicontrollers;

import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethods;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminatorParameters;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorParameters;

public class HSVIController {
    private final SolveMethods solveMethods;
    private final SolveTerminator solveTerminator;
    private final SolveTerminatorParameters solveTerminatorParameters;
    private final ExploreTerminator exploreTerminator;
    private final ExploreTerminatorParameters exploreTerminatorParameters;

    public HSVIController(SolveMethods solveMethods,
                          SolveTerminator solveTerminator,
                          ExploreTerminator exploreTerminator) {
        this.solveMethods = solveMethods;
        this.solveTerminator = solveTerminator;
        this.solveTerminatorParameters = solveTerminator.createParametersContainer();
        this.exploreTerminator = exploreTerminator;
        this.exploreTerminatorParameters = exploreTerminator.createParametersContainer();
    }

    public void init(HSVIAlgorithm hsvi) {
        solveMethods.init(hsvi);
        solveTerminator.init(hsvi, solveMethods);
        exploreTerminator.init(hsvi, solveMethods);
    }

    public void callPreSolveMethod() {
        solveMethods.callPreSolveMethod();
    }

    public void callInSolveMethod() {
        solveMethods.callInSolveMethod();
    }

    public void callPostSolveMethod() {
        solveMethods.callPostSolveMethod();
    }

    public boolean shouldSolveTerminate(double[] belief) {
        return solveTerminator.shouldTerminate(solveTerminatorParameters.setParamaters(belief));
    }

    public boolean shouldExploreTerminate(double[] belief, int t) {
        return exploreTerminator.shouldTerminate(exploreTerminatorParameters.setParameters(belief, t));
    }
}
