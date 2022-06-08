package jsat.utils.concurrent;

/**
 * @author Edward Raff
 */
public interface LoopChunkRunner {
    /**
     * Runs a process over the given loop portion
     *
     * @param start the starting index to process
     * @param end   the ending index to process
     */
    public void run(int start, int end);
}
