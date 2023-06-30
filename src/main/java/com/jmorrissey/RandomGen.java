package com.jmorrissey;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * A random generator class which takes as input a seed of numbers and their
 * associated probabilities. The method nextNum() should only ever return one of the seeded numbers and given enough
 * calls the probability of the output should converge on the seed probability.
 *
 */
public class RandomGen {

    private static final Logger LOGGER = Logger.getLogger(RandomGen.class.getName());
    private static final float DEFAULT_PROBABILITY_SUM_TOLERANCE = 0.001f;

    private final NavigableMap<Float, Integer> map = new TreeMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final float probabilitySumTolerance;

    /**
     * Constructor to initialize the random generator with numbers and their probabilities.
     *
     * @param numbers the numbers that can be returned by the random generator.
     * @param probabilities the probabilities of the occurrence of numbers.
     * @throws IllegalArgumentException if numbers and probabilities do not have the same length or if probabilities do not add up to 1.
     */
    public RandomGen(int[] numbers, float[] probabilities) {
        this(numbers, probabilities, DEFAULT_PROBABILITY_SUM_TOLERANCE);
    }

    /**
     * Constructor to initialize the random generator with numbers and their probabilities.
     *
     * @param numbers the numbers that can be returned by the random generator.
     * @param probabilities the probabilities of the occurrence of numbers.
     * @param probabilitySumTolerance the tolerance for the sum of probabilities. The sum of probabilities should be within this tolerance of 1.
     * @throws IllegalArgumentException if numbers and probabilities do not have the same length or if probabilities do not add up to 1.
     */
    public RandomGen(int[] numbers, float[] probabilities, float probabilitySumTolerance) {
        // Make a defensive copy of the input arrays - to protect against external modification outside of this class
        int[] numbersCopy = Arrays.copyOf(numbers, numbers.length);
        float[] probabilitiesCopy = Arrays.copyOf(probabilities, probabilities.length);

        validateInputs(numbersCopy, probabilitiesCopy);
        this.probabilitySumTolerance = probabilitySumTolerance;

        // using the cumulative probabilities as keys, we create a continuous range from 0 to 1 that properly reflects the probabilities of each number:
        float accumulatedProbability = 0f;
        for (int i = 0; i < numbersCopy.length; i++) {
            accumulatedProbability += probabilitiesCopy[i];
            map.put(accumulatedProbability, numbersCopy[i]);
        }
    }

    /**
     * Returns one of the initialized numbers with its assigned probability.
     *
     * @return a number from the initialized numbers.
     */
    public int nextNum() {
        lock.readLock().lock();
        try {
            float value = ThreadLocalRandom.current().nextFloat();
            return map.higherEntry(value).getValue();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void validateInputs(int[] numbers, float[] probabilities) {
        if (numbers.length != probabilities.length) {
            LOGGER.warning("The numbers and probabilities arrays must be of the same length");
            throw new IllegalArgumentException("The numbers and probabilities arrays must be of the same length");
        }

        float sum = 0f;
        for (float probability : probabilities) {
            if (probability < 0) {
                LOGGER.warning("Probabilities cannot be negative");
                throw new IllegalArgumentException("Probabilities cannot be negative");
            }
            sum += probability;
        }

        // Doesn't matter whether sum is slightly above or below 1, it just measures how far away it is from 1.
        if (Math.abs(sum - 1.0) > probabilitySumTolerance) {
            LOGGER.warning("Probabilities do not add up to 1");
            throw new IllegalArgumentException("Probabilities do not add up to 1");
        }
    }
}
