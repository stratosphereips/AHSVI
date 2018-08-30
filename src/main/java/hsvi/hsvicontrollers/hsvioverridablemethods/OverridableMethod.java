package hsvi.hsvicontrollers.hsvioverridablemethods;

import hsvi.HSVIAlgorithm;

public abstract class OverridableMethod {
    protected HSVIAlgorithm hsvi;

    public OverridableMethod() {
        hsvi = null;
    }

    public void setHsvi(HSVIAlgorithm hsvi) {
        this.hsvi = hsvi;
    }

    public abstract void callMethod();
}
