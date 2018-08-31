package hsvi.hsvicontrollers.terminators;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.InitializableWithHSVIAndSolveMethods;

public abstract class Terminator extends InitializableWithHSVIAndSolveMethods {

    public Terminator() {
        super();
    }

    public abstract boolean shouldTerminate(TerminatorParameters terminatorParameters);
}
