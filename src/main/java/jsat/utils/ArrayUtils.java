package jsat.utils;

import java.util.Arrays;
import java.util.Random;

/**
 * Extra utilities for working on array types
 *
 * @author Edward Raff
 */
public class ArrayUtils {

    /**
     * No constructing!
     */
    private ArrayUtils() {
    }

    /**
     * Converts the return value from binarySearch code such as {@link Arrays#binarySearch(double[], double)
     * } into a value that is guaranteed to be positive. It will be the either
     * the index that had the exact value, or the next position at which the
     * search value should be inserted.
     *
     * @param indx
     * @return
     */
    public static int bsIndex2Insert(int indx) {
        if (indx < 0)
            indx = -indx - 1;
        return indx;
    }

    /**
     * Swaps values in the given array
     *
     * @param array the array to swap values in
     * @param rand  the source of randomness for shuffling
     */
    static public void shuffle(int[] array, Random rand) {
        shuffle(array, 0, array.length, rand);
    }

    /**
     * Shuffles the values in the given array
     *
     * @param array the array to shuffle values in
     * @param from  the first index, inclusive, to shuffle from
     * @param to    the last index, exclusive, to shuffle from
     * @param rand  the source of randomness for shuffling
     */
    static public void shuffle(int[] array, int from, int to, Random rand) {
        for (int i = to - 1; i > from; i--)
            swap(array, i, rand.nextInt(i));
    }

    /**
     * Swaps two indices in the given array
     *
     * @param array the array to perform the sawp in
     * @param a     the first index
     * @param b     the second index
     */
    static public void swap(int[] array, int a, int b) {
        int tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }
}
