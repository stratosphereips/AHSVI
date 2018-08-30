package hsvi.hsvicontrollers;

import hsvi.HSVIAlgorithm;

public abstract class InitializableWithHSVI {
    protected HSVIAlgorithm hsvi;

    public InitializableWithHSVI() {
        hsvi = null;
    }

    public void init(HSVIAlgorithm hsvi) {
        this.hsvi = hsvi;
    }
}
