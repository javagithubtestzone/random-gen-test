package com.jmorrissey;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomGenTest {

    @ParameterizedTest
    @MethodSource("numberArrayProvider")
    void testGeneratorWithValidProbabilities(int[] numbers) {
        float[] probabilities = {0.01f, 0.3f, 0.58f, 0.1f, 0.01f};

        // generate a large number of random numbers
        RandomGen generator = new RandomGen(numbers, probabilities);
        Map<Integer, Integer> counts = new HashMap<>();
        for (int number : numbers) {
            counts.put(number, 0);
        }

        int iterations = 100000;
        for (int i = 0; i < iterations; i++) {
            int num = generator.nextNum();
            counts.put(num, counts.get(num) + 1);
        }

        // calculate the proportion of each number generated
        float[] proportions = new float[counts.size()];
        for (int i = 0; i < numbers.length; i++) {
            proportions[i] = (float) counts.get(numbers[i]) / iterations;
        }

        // assert that the proportions are roughly equal to the probabilities
        for (int i = 0; i < proportions.length; i++) {
            // Absolute difference between the observed proportions and expected probabilities is asserted to be less than 2%
            // Possibly we could move this to 1% and also increase iterations to increase confidence in the test
            assertThat(Math.abs(proportions[i] - probabilities[i])).isLessThan(0.02f);
        }
    }

    private static Collection<int[]> numberArrayProvider() {
        return Arrays.asList(
            new int[] {-2, 3, 6, 8, 7},
            new int[] {3, -2, 8, 6, 7},
            new int[] {1, 4, 0, 3, 2}
        );
    }

    @ParameterizedTest
    @ValueSource(floats = {0.9f, 2f, 0f})
    void testGeneratorWithInvalidProbabilities(float finalProbabilityOutOfRange) {
        int[] numbers = {-1, 0, 1, 2, 3};
        float[] probabilities = {0.01f, 0.3f, 0.58f, 0.1f, finalProbabilityOutOfRange};

        assertThatThrownBy(() -> new RandomGen(numbers, probabilities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Probabilities do not add up to 1");
    }

    @Test
    void testGeneratorWithUnequalLengths() {
        int[] numbers = {-1, 0, 1, 2, 3};
        float[] probabilities = {0.01f, 0.3f, 0.58f, 0.1f};

        assertThatThrownBy(() -> new RandomGen(numbers, probabilities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("The numbers and probabilities arrays must be of the same length");
    }

    @Test
    void testGeneratorWithEmptyArrays() {
        int[] numbers = {};
        float[] probabilities = {};

        assertThatThrownBy(() -> new RandomGen(numbers, probabilities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Probabilities do not add up to 1");
    }

    @Test
    void testGeneratorWithNegativeProbability() {
        int[] numbers = {1, 2, 3};
        float[] probabilities = {0.1f, -0.2f, 0.3f};

        assertThatThrownBy(() -> new RandomGen(numbers, probabilities))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Probabilities cannot be negative");
    }
}
