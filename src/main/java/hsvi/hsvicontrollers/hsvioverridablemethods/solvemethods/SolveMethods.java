package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods;

import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;

public class SolveMethods {

    private final PreSolveMethod preSolveMethod;
    private final InSolveMethod inSolveMethod;
    private final PostSolveMethod postSolveMethod;

    private int iteration;
    private long timeStarted;
    double lbVal, ubVal, lastLbVal, lastUbVal;

    public SolveMethods(PreSolveMethod preSolveMethod,
                        InSolveMethod inSolveMethod,
                        PostSolveMethod postSolveMethod) {
        this.preSolveMethod = preSolveMethod;
        this.inSolveMethod = inSolveMethod;
        this.postSolveMethod = postSolveMethod;
    }

    public void init(HSVIAlgorithm hsvi) {
        preSolveMethod.init(hsvi, this);
        inSolveMethod.init(hsvi, this);
        postSolveMethod.init(hsvi, this);
    }

    public int getIteration() {
        return iteration;
    }

    public long getTimeSinceStarted() {
        return (System.currentTimeMillis() - timeStarted) / 1000;
    }

    public double getLbVal() {
        return lbVal;
    }

    public void setLbVal(double lbVal) {
        this.lbVal = lbVal;
    }

    public void setNewLbVal(double lbVal) {
        lastLbVal = this.lbVal;
        this.lbVal = lbVal;
    }

    public double getUbVal() {
        return ubVal;
    }

    public void setUbVal(double ubVal) {
        this.ubVal = ubVal;
    }

    public void setNewUbVal(double ubVal) {
        lastUbVal = this.ubVal;
        this.ubVal = ubVal;
    }

    public double getLastLbVal() {
        return lastLbVal;
    }

    public double getLastUbVal() {
        return lastUbVal;
    }

    public void callPreSolveMethod() {
        iteration = 0;
        timeStarted = System.currentTimeMillis();
        preSolveMethod.callMethod();
    }

    public void callInSolveMethod() {
        ++iteration;
        inSolveMethod.callMethod();
    }

    public void callPostSolveMethod() {
        postSolveMethod.callMethod();
    }
}
