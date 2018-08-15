package hsvi.bounds;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class UpperBound extends Bound {

    protected List<UBPoint> points;

    public UpperBound(int dimension, Object data) {
        super(dimension, data);
        points = new LinkedList<>();
    }

    public List<UBPoint> getPoints() {
        return points;
    }

    @Override
    public int size() {
        return points.size();
    }

}
