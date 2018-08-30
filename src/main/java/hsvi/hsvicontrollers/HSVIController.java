package hsvi.hsvicontrollers;

import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminatorParameters;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorParameters;

public class HSVIController {

    private final PreSolveMethod preSolveMethod;
    private final InSolveMethod inSolveMethod;
    private final PostSolveMethod postSolveMethod;
    private final SolveTerminator solveTerminator;
    private final SolveTerminatorParameters solveTerminatorParameters;
    private final ExploreTerminator exploreTerminator;
    private final ExploreTerminatorParameters exploreTerminatorParameters;

    public HSVIController(PreSolveMethod preSolveMethod,
                          InSolveMethod inSolveMethod,
                          PostSolveMethod postSolveMethod,
                          SolveTerminator solveTerminator,
                          ExploreTerminator exploreTerminator) {
        this.preSolveMethod = preSolveMethod;
        this.inSolveMethod = inSolveMethod;
        this.postSolveMethod = postSolveMethod;
        this.solveTerminator = solveTerminator;
        this.solveTerminatorParameters = solveTerminator.createParametersContainer();
        this.exploreTerminator = exploreTerminator;
        this.exploreTerminatorParameters = exploreTerminator.createParametersContainer();
    }

    public void init(HSVIAlgorithm hsvi) {
        preSolveMethod.init(hsvi);
        inSolveMethod.init(hsvi);
        postSolveMethod.init(hsvi);
        solveTerminator.init(hsvi);
        exploreTerminator.init(hsvi);
    }

    public void callPreSolveMethod() {
        preSolveMethod.callMethod();
    }

    public void callInSolveMethod() {
        inSolveMethod.callMethod();
    }

    public void callPostSolveMethod() {
        postSolveMethod.callMethod();
    }

    public boolean shouldSolveTerminate(double[] belief) {
        return solveTerminator.shouldTerminate(solveTerminatorParameters.setParamaters(belief));
    }

    public boolean shouldExploreTerminate(double[] belief, int t) {
        return exploreTerminator.shouldTerminate(exploreTerminatorParameters.setParameters(belief, t));
    }
}
