package com.example.mathfunctions;

import java.math.BigInteger;

//2^44497-1 = 335850 ms or 5.6 min (certainty: 10); 27th Mp
//2^86243-1 = 2410454 ms or 40.17 min (certainty: 10); 28th Mp
//2^110503-1 = 4418631 ms or 73.6 min (certainty: 10); 29th Mp
//2^132049-1 = 7401334 ms or 123.4 min (certainty: 10); 30th Mp
//2^216091-1 = 31820747 ms or 530.3 min (certainty: 10); 31st Mp
//2^756839-1

public class SingleMersennePrimeEval {
    public static void main(String[] args) {
        BigInteger exponent = BigInteger.valueOf(3);
        BigInteger two = BigInteger.valueOf(2);
        BigInteger mersenneNumber = two.pow(exponent.intValue()).subtract(BigInteger.ONE);

        long startTime = System.nanoTime();
        if (probablePrime(mersenneNumber)) {
            System.out.println("The number is a prime: " + mersenneNumber);
        } else {
            System.out.println("The number is not a prime.");
        }
        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
        System.out.println("Execution time: " + duration + " milliseconds");
    }

    //uses Miller-Rabin primality testing and for numbers greater than 100 bits, Lucas-Lehmer primality testing
    private static boolean probablePrime(BigInteger value) {
        // Check if a solution exists for formula1 only
        return value.isProbablePrime(10);
    }
}