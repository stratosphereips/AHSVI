package ahsvi;

import hsvi.Config;
import hsvi.HSVIAlgorithm;
import pomdpproblem.pomdpdummyproblems.POMDPDummyProblems;
import org.junit.Test;

import static org.junit.Assert.*;

public class AHSVITest {
    // generic tests
    public void testUBGteUB(double ub, double lb) {
        assertTrue("UB must be greater than or equal LB",
                ub >= lb);
    }

    public void testUBCloseToLB(double ub, double lb, double eps) {
        assertTrue("UB must be max epsilon from LB",
                ub - lb <= eps);
    }

    public HSVIAlgorithm testDummyPOMDP(String pomdpProblemName, double epsilon) {
        return testDummyPOMDP(pomdpProblemName, epsilon, null);
    }

    public HSVIAlgorithm testDummyPOMDP(String pomdpProblemName, double epsilon, Double expectedValueInInitBelief) {
        System.out.printf("* TESTING \"%s\" DUMMY POMDP PROBLEM\n", pomdpProblemName);
        HSVIAlgorithm hsviAlgorithm = new HSVIAlgorithm(new POMDPDummyProblems(pomdpProblemName).load(), epsilon);
        hsviAlgorithm.solve();
        testUBGteUB(hsviAlgorithm.getUBValueInInitBelief(), hsviAlgorithm.getLBValueInInitBelief());
        testUBCloseToLB(hsviAlgorithm.getUBValueInInitBelief(), hsviAlgorithm.getLBValueInInitBelief(), epsilon);
        if (expectedValueInInitBelief != null) {
            assertEquals(expectedValueInInitBelief, hsviAlgorithm.getUBValueInInitBelief(), epsilon);
        }
        return hsviAlgorithm;
    }

    @Test(timeout = 300)
    public void test1SPOMDP() {
        String pomdpProblemName = "1s";
        double epsilon = Config.ZERO;
        double expectedValueInInitBelief = 4;
        HSVIAlgorithm hsviAlgorithm = testDummyPOMDP(pomdpProblemName, epsilon, expectedValueInInitBelief);
    }

    @Test(timeout = 300)
    public void test2SPOMDP() {
        String pomdpProblemName = "2s";
        double epsilon = Config.ZERO;
        double expectedValueInInitBelief = 2.2857135;
        HSVIAlgorithm hsviAlgorithm = testDummyPOMDP(pomdpProblemName, epsilon, expectedValueInInitBelief);
    }

    @Test(timeout = 1000)
    public void test1DPOMDP() {
        String pomdpProblemName = "1d";
        double epsilon = Config.ZERO;
        double expectedValueInInitBelief = 1.3609185;
        HSVIAlgorithm hsviAlgorithm = testDummyPOMDP(pomdpProblemName, epsilon, expectedValueInInitBelief);
    }

    @Test(timeout = 300)
    public void testTigerPOMDP() {
        String pomdpProblemName = "tiger";
        double epsilon = Config.ZERO;
        double expectedValueInInitBelief = -10;
        HSVIAlgorithm hsviAlgorithm = testDummyPOMDP(pomdpProblemName, epsilon, expectedValueInInitBelief);
    }
}
