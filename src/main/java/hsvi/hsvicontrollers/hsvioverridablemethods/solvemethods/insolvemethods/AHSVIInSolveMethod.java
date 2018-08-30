package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

public class AHSVIInSolveMethod extends InSolveMethod {
    public AHSVIInSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        super.callMethod();
        hsvi.getPomdpProblem().setInitBelief(hsvi.getLbFunction().getBeliefInMinimum());
    }
}
