package hsvi;

import pomdpproblem.POMDPProblem;

import java.util.Arrays;

public class AHSVIAlgorithm extends HSVIAlgorithm{

    public AHSVIAlgorithm(POMDPProblem pomdpProblem, double epsilon) {
        super(pomdpProblem, epsilon);
    }

    @Override
    public void solve() {
        double[] beliefMinLb = lbFunction.getBeliefInMinimum();
        double valueInBeliefMinLb = lbFunction.getValue(beliefMinLb);
        double[] beliefMinUb = ubFunction.getBeliefInMinimum();
        double valueInBeliefMinUb = ubFunction.getValue(beliefMinUb);

        System.out.println("Belief in LB min: " + Arrays.toString(beliefMinLb) + " => " + valueInBeliefMinLb);
        System.out.println("Belief in UB min: " + Arrays.toString(beliefMinUb) + " => " + valueInBeliefMinUb);

        long timeStarted = System.currentTimeMillis();
        int iter = 0;
        double lastLbVal, lastUbVal;
        System.out.println("###########################################################################");
        System.out.println("###########################################################################");
        ++iter;
        System.out.println("Solve iteration: " + iter);
        System.out.println("Belief in LB min: " + Arrays.toString(beliefMinLb) + " => " + valueInBeliefMinLb);
        System.out.println("Belief in UB min: " + Arrays.toString(beliefMinUb) + " => " + valueInBeliefMinUb);
        lastLbVal = valueInBeliefMinLb;
        lastUbVal = valueInBeliefMinUb;
        System.out.println("===========================================================================");
        while ((valueInBeliefMinUb - valueInBeliefMinLb) / valueInBeliefMinUb > epsilon) {
            explore(beliefMinLb, 0, iter);

            beliefMinLb = lbFunction.getBeliefInMinimum();
            valueInBeliefMinLb = lbFunction.getValue(beliefMinLb);
            beliefMinUb = ubFunction.getBeliefInMinimum();
            valueInBeliefMinUb = ubFunction.getValue(beliefMinUb);

            System.out.println("###########################################################################");
            System.out.println("###########################################################################");
            ++iter;
            System.out.println("Solve iteration: " + iter);
            System.out.println("Belief in LB min: " + Arrays.toString(beliefMinLb) + " => " + valueInBeliefMinLb);
            System.out.printf(" ----- Diff to last iteration: %.20f\n", (valueInBeliefMinLb - lastLbVal));
            System.out.println("LB size: " + lbFunction.getAlphaVectors().size());
            System.out.println("Belief in UB min: " + Arrays.toString(beliefMinUb) + " => " + valueInBeliefMinUb);
            System.out.printf(" ----- Diff to last iteration: %.20f\n", (valueInBeliefMinUb - lastUbVal));
            System.out.println("UB size: " + ubFunction.getPoints().size());
            System.out.println("Running time so far [s]: " + ((System.currentTimeMillis() - timeStarted) / 1000));
            lastLbVal = valueInBeliefMinLb;
            lastUbVal = valueInBeliefMinUb;
            System.out.println(lbFunction);
            System.out.println(ubFunction);
            System.out.println("===========================================================================");
        }
    }

    @Override
    protected boolean exploreEndingCondition(double[] belief, int t, int iteration) {
        return t >= Math.sqrt(iteration);
    }
}
