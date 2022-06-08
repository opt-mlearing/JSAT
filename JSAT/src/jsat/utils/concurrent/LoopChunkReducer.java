package jsat.utils.concurrent;

import java.util.function.BinaryOperator;

/**
 * This functional interface is similar to that of {@link LoopChunkReducer},
 * allowing convieniently processing of a range of values for parallel
 * computing. However, the Reducer returns an object implementing the
 * {@link BinaryOperator} interface. The goal is that all chunks will eventually
 * be merged into a single result. This interface is preffered over using normal
 * streams to reduce unecessary object creation and reductions.
 *
 * @param <T> The object type that the Loop Chunk Reducer will return
 * @author Edward Raff
 */
public interface LoopChunkReducer<T> {
    /**
     * Runs a process over the given loop portion, returning a single object of type {@link T}.
     *
     * @param start the starting index to process
     * @param end   the ending index to process
     * @return the object to return
     */
    public T run(int start, int end);
}
