package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

import java.util.Arrays;

public class AHSVIInSolveMethod extends InSolveMethod {

    private final AHSVIMinValueFinder minValueFinder;

    public AHSVIInSolveMethod(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }

    @Override
    public void callMethod() {
        super.callMethod();
        hsvi.getPomdpProblem().setInitBelief(minValueFinder.findBeliefInLbMin());

        System.out.println(Arrays.toString(hsvi.getPomdpProblem().initBelief));
    }
}
