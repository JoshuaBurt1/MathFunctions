package com.example.mathfunctions;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class  PiCalculations {
    public static void main(String[] args) {
        double standard = 3.1415926535897932384626;

        //PI CALCULATION: Madhava-Leibniz series
        long startTime1 = System.nanoTime();
        double piCalc1 = 0;
        for (int i = 0; i <= 10000; i++) {
            piCalc1 += (2.0 / ((4 * i + 1) * (4 * i + 3)));
        }
        piCalc1 = piCalc1 * 4;
        long endTime1 = System.nanoTime();
        System.out.println("Madhava-Leibniz Sum");
        System.out.println("Pi: " + piCalc1 + " Deviation: " + (piCalc1 - standard));
        long elapsedTime1 = endTime1 - startTime1;
        System.out.println("Time taken: " + elapsedTime1 + " nanoseconds \n");

        //PI CALCULATION: Madhava-Leibniz series fast converge
        long startTime3 = System.nanoTime();
        double piCalc2 = 3;
        double piCalc3 = 0;

        for (int i = 3; i <= 1476; i += 4) { //369 iterations
            piCalc2 += 4.0 / ((i * i * i) - i);
            piCalc3 -= 4.0 / (((i + 2) * (i + 2) * (i + 2)) - (i + 2));
        }
        piCalc2 = piCalc2 + piCalc3;
        long endTime3 = System.nanoTime();
        System.out.println("Madhava-Leibniz Sum fast converge");
        System.out.println("Pi: " + piCalc2 + " Deviation: " + (piCalc2 - standard));
        long elapsedTime3 = endTime3 - startTime3;
        System.out.println("Time taken: " + elapsedTime3 + " nanoseconds \n");

        //PI CALCULATION: Wallis product
        long startTime2 = System.nanoTime();
        double piCalc4 = 1;
        for (int i = 1; i <= 10000; i++) {
            piCalc4 *= ((4.0 * i * i) / ((4.0 * i * i) - 1));
        }
        double finalA = 2 * piCalc4;
        long endTime2 = System.nanoTime();
        System.out.println("Wallis Product");
        System.out.println("Pi: " + finalA + " Deviation: " + (finalA - standard));
        long elapsedTime2 = endTime2 - startTime2;
        System.out.println("Time taken: " + elapsedTime2 + " nanoseconds\n");

        //PI CALCULATION: Wallis product squares
        long startTime5 = System.nanoTime();
        double piCalc5 = 1;
        double piCalc6 = 1;
        for (int i = 2; i <= 20000; i += 2) {
            piCalc5 *= ((1.0 * ((i + 1) * (i + 1) - 1)) / (1.0 * ((i + 1) * (i + 1)))); //from above
            piCalc6 *= ((1.0 * (i * i)) / (1.0 * (i * i) - 1)); //from below
        }
        double finalA5 = 2 * piCalc5 + piCalc6;
        long endTime5 = System.nanoTime();
        System.out.println("Wallis Product squares");
        System.out.println("Pi: " + finalA5 + " Deviation: " + (finalA5 - standard));
        long elapsedTime5 = endTime5 - startTime5;
        System.out.println("Time taken: " + elapsedTime5 + " nanoseconds\n");

        //PI CALCULATION: Brouncker forumula: 1/(1+1^2/(2+3^2/(2+5^2/(2+7^2
        long startTime8 = System.nanoTime();
        double piCalc8 = 1.0;
        for (int i = 20001; i >= 1; i -= 2) {
            piCalc8 = i * i / (2 + piCalc8);
        }
        piCalc8 = 4 * (1.0 / (1 + piCalc8));
        long endTime8 = System.nanoTime();
        System.out.println("Brouncker formula");
        System.out.println("Pi: " + piCalc8 + " Deviation: " + (piCalc8 - standard));
        long elapsedTime8 = endTime8 - startTime8;
        System.out.println("Time taken: " + elapsedTime8 + " nanoseconds\n");

        //*******************
        // PI CALCULATION: GAUSS-LEGENDRE
        long startTimeGaussA = System.nanoTime();
        double piGaussA = 0.0;

        for (int k = 0; k < 10; k++) {
            double term1 = 4.0 / (8 * k + 1);
            double term2 = 2.0 / (8 * k + 4);
            double term3 = 1.0 / (8 * k + 5);
            double term4 = 1.0 / (8 * k + 6);
            double term = term1 - term2 - term3 - term4;
            term = term * Math.pow(1.0 / 16, k);
            piGaussA += term;
        }

        long endTimeGaussA = System.nanoTime();
        System.out.println("Gauss-Legendre Algorithm");
        System.out.println("Pi: " + piGaussA);

        System.out.println("Deviation: " + (piGaussA - standard));
        long elapsedGaussA = endTimeGaussA - startTimeGaussA;
        System.out.println("Time taken: " + elapsedGaussA + " nanoseconds\n");

        // PI CALCULATION: GAUSS-LEGENDRE (BigDecimal)
        MathContext mathContext = new MathContext(100); // Set precision to 25 decimal places for standard, adjust as needed
        long startTimeGauss = System.nanoTime();
        BigDecimal piGauss = BigDecimal.ZERO;

        for (int k = 0; k < 78; k++) {
            BigDecimal term1 = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8 * k + 1), mathContext);
            BigDecimal term2 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8 * k + 4), mathContext);
            BigDecimal term3 = BigDecimal.ONE.divide(BigDecimal.valueOf(8 * k + 5), mathContext);
            BigDecimal term4 = BigDecimal.ONE.divide(BigDecimal.valueOf(8 * k + 6), mathContext);
            BigDecimal term = term1.subtract(term2).subtract(term3).subtract(term4);
            term = term.multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(16).pow(k), mathContext));
            piGauss = piGauss.add(term);
        }
        long endTimeGauss = System.nanoTime();
        System.out.println("Gauss-Legendre Algorithm (using BigDecimal)");
        System.out.println("Pi: " + piGauss.setScale(100, RoundingMode.HALF_UP));
        BigDecimal standard2 = new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067");

        System.out.println("Pi: " + standard2.setScale(100, RoundingMode.HALF_UP));
        System.out.println("Deviation: " + piGauss.subtract(standard2));
        long elapsedGauss = endTimeGauss - startTimeGauss;
        System.out.println("Time taken: " + elapsedGauss + " nanoseconds\n");
    }
}