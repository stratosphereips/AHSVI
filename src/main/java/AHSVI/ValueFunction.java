package main.java.AHSVI;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public abstract class ValueFunction {
    int dimension;
    Object data;

    public ValueFunction(int dimension, Object data) {
        this.dimension = dimension;
        this.data = data;
    }

    public abstract double getValue(double[] point);

    public abstract IloRange constructLP(IloCplex cplex, IloNumVar[] coords, IloNumVar value) throws IloException;

    public Object getData() {
        return data;
    }
}
