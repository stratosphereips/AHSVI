package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

public class AHSVIPreSolveMethod extends PreSolveMethod {
    private final AHSVIMinValueFinder minValueFinder;

    public AHSVIPreSolveMethod(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }
    @Override
    public void callMethod() {
        super.callMethod();
        minValueFinder.setHsvi(hsvi);
        hsvi.getPomdpProblem().setInitBelief(minValueFinder.findBeliefInLbMin());
    }
}
