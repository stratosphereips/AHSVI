package hsvi;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * Created by wigos on 27. 11. 2015.
 */
public class Cplex {
    private static IloCplex cplex = null;
    public static IloCplex get() throws IloException {
        if(cplex == null) cplex = new IloCplex();
        return cplex;
    }

    private Cplex() {}

    public static int addCols(IloLPMatrix matrix, IloNumVar[]... vars) throws IloException {
        int totalVars = 0;
        for(int i = 0 ; i < vars.length ; i++) totalVars += vars[i].length;

        IloNumVar[] cols = new IloNumVar[totalVars];
        int offset = 0;
        for(int i = 0 ; i < vars.length ; i++) {
            for(int j = 0 ; j < vars[i].length ; j++) {
                cols[offset++] = vars[i][j];
            }
        }

        return matrix.addCols(cols);
    }
}
