package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods;

import hsvi.HSVIAlgorithm;
import hsvi.bounds.LBAlphaVector;
import hsvi.bounds.UBPoint;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.LinkedList;

public class AHSVIMinValueFinder {
    private HSVIAlgorithm hsvi;
    private final ArrayList<LinkedList<Integer>> groups;
    private final double[] groupsProbabilities;

    public AHSVIMinValueFinder(ArrayList<LinkedList<Integer>> groups, double[] groupsProbabilities) {
        this.groups = groups;
        this.groupsProbabilities = groupsProbabilities;
        hsvi = null;
    }

    public void setHsvi(HSVIAlgorithm hsvi) {
        this.hsvi = hsvi;
    }

    public double[] findBeliefInLbMin() {
        // TODO make this smarter
        try {
            IloCplex model = new IloCplex();
            model.setOut(null);
            model.setWarning(null);

            IloNumExpr obj = model.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            IloNumVar[] beliefVars = model.numVarArray(hsvi.getPomdpProblem().getNumberOfStates(), 0.0, 1.0);
            model.addEq(model.sum(beliefVars), 1.0);
            // TODO MIN BELIEF
            IloNumExpr statesProbabilitiesConstr;
            for (LinkedList<Integer> group : groups) {

            }

            for (LBAlphaVector alphaVector : hsvi.getLbFunction().getAlphaVectors()) {
                model.addGe(obj, model.scalProd(alphaVector.vector, beliefVars));
            }
            //model.exportModel("min_belief.lp");
            model.addMinimize(obj);
            model.solve();
            return model.getValues(beliefVars);

        } catch (IloException e) {
            e.printStackTrace();
            System.exit(10);
        }

        return null;
    }

    public double[] findBeliefInUbMin() {
        // TODO just find min among UB points?
        double[] beliefInMin = null;
        double minValue = Double.POSITIVE_INFINITY;
        for (UBPoint point : hsvi.getUbFunction().getPoints()) {
            if (point.getValue() < minValue) {
                minValue = point.getValue();
                beliefInMin = point.getBelief();
            }
        }
        return beliefInMin;
    }
}
