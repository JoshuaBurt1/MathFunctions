package com.example.mathfunctions;

import java.math.BigInteger;

// Lucas-Lehmer primality test is faster for p > (16383 = 2^14-1) -> faster for n > 2^(2^14-1)-1
// Miller-Rabin primality test is faster for p <= (16383 = 2^14-1) -> faster for n < 2^(2^14-1)-1
//Lucas-Lehmer  
//16291 : 2101 ms   slower
//16383 : 2199 ms   slower
//16387 : 2226 ms   faster
//16391 : 2123 ms   faster
//Miller-Rabin
//16291 : 1711 ms     11111110100011
//16383 : 1755 ms     11111111111111 (14 digits)
//16387 : 2716 ms
//16391 : 2633 ms

//For single prime evaluation set the exponent value and exponent value range to the same value
public class SinglePrimeEval {
    public static void main(String[] args) {
        long totalStartTimeB = System.nanoTime();

        for (int exponentValue = 16483; exponentValue <= 16483; exponentValue++) {

            BigInteger exponent = BigInteger.valueOf(exponentValue);
            BigInteger two = BigInteger.valueOf(2);
            BigInteger mersenneNumber = two.pow(exponent.intValue()).subtract(BigInteger.ONE);

            // Measure execution time for Lucas-Lehmer test
            long startTimeLucas1 = System.nanoTime();
            if(!lucasLehmerOriginal(exponent)) {
                System.out.println("Composite");
            }
            long endTimeLucas1 = System.nanoTime();
            long durationLucas1 = (endTimeLucas1 - startTimeLucas1) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("Execution time (Lucas-Lehmer original): " + durationLucas1 + " milliseconds");

            // Measure execution time for Lucas-Lehmer test
            long startTimeLucas = System.nanoTime();
            if(!lucasLehmerTest(exponent)) {
                System.out.println("Composite");
            }
            long endTimeLucas = System.nanoTime();
            long durationLucas = (endTimeLucas - startTimeLucas) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("Execution time (Lucas-Lehmer test): " + durationLucas + " milliseconds");

            /*
            // Measure execution time for BigInteger.isProbablePrime
            long startTime = System.nanoTime();
            if(!probablePrime(mersenneNumber)) {
                System.out.println("Composite");
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("Execution time (BigInteger.isProbablePrime): " + duration + " milliseconds");
             */

            // Measure execution time for custom Miller-Rabin with base 3
            long startTimeB = System.nanoTime();
            if(!probablePrimeCustom(mersenneNumber, 3)) {
                System.out.println("Composite");
            }
            long endTimeB = System.nanoTime();
            long durationB = (endTimeB - startTimeB) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("Execution time (Custom Miller-Rabin with base 3): " + durationB + " milliseconds");

        }
        long totalEndTimeB = System.nanoTime();
        long totalDuration = (totalEndTimeB - totalStartTimeB) / 1_000_000; // Convert nanoseconds to milliseconds
        System.out.println("Total time: " + totalDuration + " milliseconds");

    }

    /*
    // Uses Miller-Rabin primality testing (BigInteger default method)
    private static boolean probablePrime(BigInteger value) {
        // Check if a solution exists for formula1 only
        if(value.isProbablePrime(10)){
            System.out.println("The number is a prime: " + value);
            return true;
        }
        System.out.println("The number is not a prime: " + value);

        return false;
    }*/

    // Miller-Rabin Primality test using a specific base
    private static boolean probablePrimeCustom(BigInteger value, int base) {
        // If base is greater than the number, it's automatically composite
        if (BigInteger.valueOf(base).compareTo(value) >= 0) {
            return false;
        }

        // Compute value-1, used in the test
        BigInteger d = value.subtract(BigInteger.ONE);
        int s = 0;
        // Decompose value - 1 as d * 2^s
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.shiftRight(1);
            s++;
        }

        // Compute a^d mod value
        BigInteger a = BigInteger.valueOf(base);
        BigInteger x = a.modPow(d, value);

        if (x.equals(BigInteger.ONE) || x.equals(value.subtract(BigInteger.ONE))) {
            System.out.println("The number is a prime: " + value);
            return true;
        }

        // Perform s-1 squaring iterations
        for (int r = 0; r < s - 1; r++) {
            x = x.modPow(BigInteger.TWO, value); // x = x^2 mod value
            if (x.equals(value.subtract(BigInteger.ONE))) {
                System.out.println("The number is a prime: " + value);
                return true;
            }
        }
        // If no witness was found, the number is composite
        System.out.println("The number is not a prime: " + value);
        return false;
    }

    private static boolean lucasLehmerTest(BigInteger exponent) {
        //if (exponent.equals(BigInteger.ONE)) {
        //    System.out.println("The number is composite: " + (BigInteger.valueOf(2).pow(1).subtract(BigInteger.ONE)));
        //    return false;  // 2^1 - 1 = 1, not prime
        //}

        // Mersenne number: M_p = 2^p - 1
        BigInteger mersenneNumber = BigInteger.ONE.shiftLeft(exponent.intValue()).subtract(BigInteger.ONE);

        // Initial value for S_0
        BigInteger s = BigInteger.valueOf(4);
        BigInteger two = BigInteger.valueOf(2);

        // Apply recurrence relation S_n = (S_{n-1}^2 - 2) % M_p
        for (BigInteger n = BigInteger.ONE; n.compareTo(exponent.subtract(BigInteger.ONE)) < 0; n = n.add(BigInteger.ONE)) {
            s = s.multiply(s).subtract(two); // S_{n-1}^2 - 2
            s = s.mod(mersenneNumber); // Modulo M_p
        }

        // Check if S_{p-2} % M_p == 0
        if (s.equals(BigInteger.ZERO)) {
            System.out.println("The number is a Mersenne prime: " + mersenneNumber);
            return true;
        } else {
            System.out.println("The number is composite: " + mersenneNumber);
            return false;
        }
    }

    // Lucas-Lehmer Primality test for Mersenne numbers
    private static boolean lucasLehmerOriginal(BigInteger exponent) {
        // Mersenne number: M_p = 2^p - 1
        BigInteger two = BigInteger.valueOf(2);
        BigInteger mersenneNumber = two.pow(exponent.intValue()).subtract(BigInteger.ONE);

        // Initial value for S_0
        BigInteger s = BigInteger.valueOf(4);

        // Apply recurrence relation S_n = (S_{n-1}^2 - 2) % M_p
        for (BigInteger n = BigInteger.ONE; n.compareTo(exponent.subtract(BigInteger.ONE)) < 0; n = n.add(BigInteger.ONE)) {
            s = s.multiply(s).subtract(BigInteger.valueOf(2)); // S_{n-1}^2 - 2
            s = s.mod(mersenneNumber); // Modulo M_p
        }

        // Check if S_{p-2} % M_p == 0
        if (s.equals(BigInteger.ZERO)) {
            System.out.println("The number is a Mersenne prime: " + mersenneNumber);
            return true;
        } else {
            System.out.println("The number is composite: " + mersenneNumber);
            return false;
        }
    }
}

//27th Mp : 2^44497-1 = 335850 ms / 5.6 min //value.isProbablePrime(10); hardware: i7
//27th Mp : 2^44497-1 = 166667 ms / 2.77 min //value.isProbablePrime(10); hardware: i9
//27th Mp : 2^44497-1 = 49714 ms / 0.82 min; //Custom Miller-Rabin Primality test base 3; hardware: i9
//27th Mp : 2^44497-1 = 26981 ms / 0.449 min; //Lucas-Lehmer Primality test; hardware: i9

//28th Mp : 2^86243-1 = 2410454 ms or 40.17 min //value.isProbablePrime(10); hardware: i7
//28th Mp : 2^86243-1 = 1113787 ms or 18.56 min //value.isProbablePrime(10); hardware: i9
//28th Mp : 2^86243-1 = 390527 ms or 6.51 min //Custom Miller-Rabin Primality test base 3; hardware: i9
//28th Mp : 2^86243-1 = 183914 ms / 3.06 min; //Lucas-Lehmer Primality test; hardware: i9
