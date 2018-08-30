package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

public class AHSVIPreSolveMethod extends PreSolveMethod {
    public AHSVIPreSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        super.callMethod();
        hsvi.getPomdpProblem().setInitBelief(hsvi.getLbFunction().getBeliefInMinimum());
    }
}
