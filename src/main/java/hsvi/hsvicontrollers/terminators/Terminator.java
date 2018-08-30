package hsvi.hsvicontrollers.terminators;

import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.InitializableWithHSVI;

public abstract class Terminator extends InitializableWithHSVI {

    public Terminator() {
        super();
    }

    public abstract boolean shouldTerminate(TerminatorParameters terminatorParameters);
}
