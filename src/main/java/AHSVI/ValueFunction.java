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

    public double[] cut(double[] b1, double[] b2) {
        return cut(b1, b2, 100); // TODO proc 100
    }

    public double[] cut(double[] b1, double[] b2, int numPoints) {
        int ins = 0;
        double ret[] = new double[numPoints];
        for (double a = 0.0; a <= 1.0 + 1e-4; a += 1.0 / (numPoints - 1)) {
            a = Math.min(a, 1.0);
            double[] b = new double[b1.length];
            for (int i = 0; i < b1.length; ++i) {
                b[i] = a * b1[i] + (1 - a) * b2[i];
            }
            ret[ins] = getValue(b);
            ++ins;
        }
    }
}
