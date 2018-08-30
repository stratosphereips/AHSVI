package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.hsvicontrollers.hsvioverridablemethods.OverridableMethod;

public class PreSolveMethod extends OverridableMethod {

    public PreSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        hsvi.initValueFunctions();
        hsvi.initSolveIteration();
    }
}
