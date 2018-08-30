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

    PreSolveMethod preSolveMethod;
    InSolveMethod inSolveMethod;
    PostSolveMethod postSolveMethod;
    SolveTerminator solveTerminator;
    SolveTerminatorParameters solveTerminatorParameters;
    ExploreTerminator exploreTerminator;
    ExploreTerminatorParameters exploreTerminatorParameters;

    public HSVIController() {
        preSolveMethod = null;
        inSolveMethod = null;
        postSolveMethod = null;
        solveTerminator = null;
        solveTerminatorParameters = null;
        exploreTerminator = null;
        exploreTerminatorParameters = null;
    }

    public HSVIController setPreSolveMethod(PreSolveMethod preSolveMethod) {
        this.preSolveMethod = preSolveMethod;
        return this;
    }

    public HSVIController setInSolveMethod(InSolveMethod inSolveMethod) {
        this.inSolveMethod = inSolveMethod;
        return this;
    }

    public HSVIController setPostSolveMethod(PostSolveMethod postSolveMethod) {
        this.postSolveMethod = postSolveMethod;
        return this;
    }

    public HSVIController setSolveTerminator(SolveTerminator solveTerminator) {
        this.solveTerminator = solveTerminator;
        solveTerminatorParameters = solveTerminator.createParametersContainer();
        return this;
    }

    public HSVIController setExploreTerminator(ExploreTerminator exploreTerminator) {
        this.exploreTerminator = exploreTerminator;
        exploreTerminatorParameters = exploreTerminator.createParametersContainer();
        return this;
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

    public boolean shouldExploreTerminate(double[] belief, int t, int iteration) {
        return exploreTerminator.shouldTerminate(exploreTerminatorParameters.setParameters(belief, t, iteration));
    }
}
