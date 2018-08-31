package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods;

import hsvi.HSVIAlgorithm;
import hsvi.bounds.LBAlphaVector;
import hsvi.bounds.UBPoint;
import hsvi.hsvicontrollers.InitializableWithHSVI;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.LinkedList;

public class AHSVIMinValueFinder extends InitializableWithHSVI {
    private final ArrayList<LinkedList<Integer>> statesGroups;
    private final double[] groupsProbabilities;

    public AHSVIMinValueFinder(ArrayList<LinkedList<Integer>> statesGroups, double[] groupsProbabilities) {
        this.statesGroups = statesGroups;
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
            IloNumExpr statesProbabilitiesSum;
            LinkedList<Integer> group;
            for (int groupI = 0; groupI < statesGroups.size(); ++groupI) {
                group = statesGroups.get(groupI);
                statesProbabilitiesSum = model.constant(0);
                for (Integer s : group) {
                    statesProbabilitiesSum = model.sum(statesProbabilitiesSum, beliefVars[s]);
                }
                model.addEq(statesProbabilitiesSum, groupsProbabilities[groupI]);
            }

            for (LBAlphaVector alphaVector : hsvi.getLbFunction().getAlphaVectors()) {
                model.addGe(obj, model.scalProd(alphaVector.vector, beliefVars));
            }
            model.exportModel("min_belief.lp");
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
        // TODO make this smarter
        try {
            IloCplex model = new IloCplex();
            model.setOut(null);
            model.setWarning(null);

            IloNumExpr valueVar = model.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            int numberOfStates = hsvi.getPomdpProblem().getNumberOfStates();
            IloNumVar[] beliefVars = model.numVarArray(numberOfStates, 0.0, 1.0);
            ArrayList<UBPoint> ubPoints = new ArrayList<>(hsvi.getUbFunction().getPoints());
            IloNumVar[] coefVars = model.numVarArray(ubPoints.size(), 0.0, 1.0);

            model.addEq(model.sum(coefVars), 1.0);
            model.addEq(model.sum(beliefVars), 1.0);
            model.addEq(beliefVars[beliefVars.length - 1], 0.0);

            UBPoint point;
            IloNumExpr valueSum = model.constant(0);
            IloNumExpr[] beliefSums = new IloNumExpr[numberOfStates];
            for (int s = 0; s < numberOfStates; ++s) {
                beliefSums[s] = model.constant(0);
            }

            double[] beliefInPoint;
            for (int pointI = 0; pointI < ubPoints.size(); ++pointI) {
                point = ubPoints.get(pointI);
                beliefInPoint = point.getBelief();
                for (int s = 0; s < numberOfStates; ++s) {
                    beliefSums[s] = model.sum(beliefSums[s], model.prod(coefVars[pointI], beliefInPoint[s]));
                }
                valueSum = model.sum(valueSum, model.prod(coefVars[pointI], point.getValue()));
            }

            for (int s = 0; s < numberOfStates; ++s) {
                model.addEq(beliefSums[s], beliefVars[s]);
            }
            model.addEq(valueSum, valueVar);

            model.addMinimize(valueVar);
            model.solve();
            return model.getValues(beliefVars);

        } catch (IloException e) {
            e.printStackTrace();
            System.exit(10);
        }
        return null;
    }
}
