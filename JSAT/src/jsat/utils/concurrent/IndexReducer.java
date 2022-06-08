package jsat.utils.concurrent;

/**
 * @param <T> The object type that the IndexReducer will return
 * @author Edward Raff
 */
public interface IndexReducer<T> {
    public T run(int indx);
}
