package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods;

import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.InitializableWithHSVI;

public class InitializableWithHSVIAndSolveMethods extends InitializableWithHSVI {

    protected SolveMethods solveMethodsContainer;

    public InitializableWithHSVIAndSolveMethods() {
        super();
        solveMethodsContainer = null;
    }

    public void init(HSVIAlgorithm hsvi, SolveMethods solveMethods) {
        super.init(hsvi);
        this.solveMethodsContainer = solveMethods;
    }
}
