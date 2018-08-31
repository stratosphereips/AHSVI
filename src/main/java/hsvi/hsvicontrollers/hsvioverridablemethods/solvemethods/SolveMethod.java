package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods;

import hsvi.hsvicontrollers.hsvioverridablemethods.OverridableMethod;

public abstract class SolveMethod extends InitializableWithHSVIAndSolveMethods implements OverridableMethod {
    public SolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        solveStageSpecificMethod();
        overridableMethod();
    }

    protected abstract void solveStageSpecificMethod();

    protected abstract void overridableMethod();
}
