package HSVI;

public interface Dominable<T> {
    Object parent = null;

    void dominated(T other);
    void setParent(Object parent);
    Object getParent();
}
