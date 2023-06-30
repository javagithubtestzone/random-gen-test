package com.jmorrissey;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomGenApplication {

    private static final Logger LOGGER = Logger.getLogger(RandomGenApplication.class.getName());

    public static void main(String[] args) {
        int[] numbers = {-1, 0, 1, 2, 3};
        float[] probabilities = {0.01f, 0.3f, 0.58f, 0.1f, 0.01f};

        RandomGen randomGen = new RandomGen(numbers, probabilities);
        Map<Integer, Integer> counts = new HashMap<>();
        for (int number : numbers) {
            counts.put(number, 0);
        }

        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            int num = randomGen.nextNum();
            counts.put(num, counts.get(num) + 1);
        }

        StringBuilder sb = new StringBuilder();
        for (int number : numbers) {
            sb.append(number).append(": ").append(counts.get(number)).append(" times").append(System.lineSeparator());
        }

        LOGGER.log(Level.INFO, """
                                    Counts:
                                    {0}""", sb);
    }
}