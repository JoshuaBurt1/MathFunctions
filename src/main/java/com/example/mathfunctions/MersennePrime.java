package com.example.mathfunctions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Checks for low factors. Then use probabilistic isProbablePrime() method to check higher numbers. --> balancing factorization time : isProbablePrime() time
// an array size of 1 would still be faster than purely probabilistic prime detection
// time                     primes        numberOfTerms                                    checkFactors()
// 166290ms/2.77 minutes   (2+21 primes); 1000 iterations  //value.isProbablePrime(10);  arraySize = 10000;
// 2214645ms/36.91 minutes (2+22 primes); 1750 iterations  //value.isProbablePrime(10);  arraySize = 50000;
// 4366065ms/72.77 minutes (2+24 primes); 2000 iterations  //value.isProbablePrime(10);  arraySize = 100000;
// TODO: 1. add calculation for "Up to what prime would you like to search for?", 2. Print equation next to prime

public class MersennePrime {
    static int countFactor = 0;
    static int countTotal = 0;
    static ArrayList<BigInteger> unfactoredNumbers = new ArrayList<>();
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println("Generating exponents.");
        ArrayList<BigInteger> exponentList1 = generateExponentList1(1000);
        ArrayList<BigInteger> exponentList2 = generateExponentList2(1000);
        ArrayList<BigInteger> exponentList3 = generateExponentList3(1000);
        ArrayList<BigInteger> exponentList4 = generateExponentList4(1000);
        System.out.println("Beginning factorization.");
        for (BigInteger p : exponentList1) {
            countFactor += checkFactors_5(p);
        }
        System.out.println("5 origin factorization complete.");
        for (BigInteger p : exponentList2) {
            countFactor += checkFactors_7(p);
        }
        System.out.println("7 origin factorization complete.");
        for (BigInteger p : exponentList3) {
            countFactor += checkFactors_11(p);
        }
        System.out.println("11 origin factorization complete.");
        for (BigInteger p : exponentList4) {
            countFactor += checkFactors_13(p);
        }
        System.out.println("13 origin factorization complete.");

        System.out.println("Total with at least one 'low' factor: " + countFactor + "/" +countTotal);
    /*System.out.println("List of unfactored numbers");
    for (int i =0; i<unfactoredNumbers.size();i++){
        System.out.print(unfactoredNumbers.get(i) +"\n");
    }*/

        System.out.println("\nPrime Number List:");
        List<BigInteger> primeNumberList = filterPrimes(unfactoredNumbers);
        printList(primeNumberList);

        long endTime = System.nanoTime(); // Capture end time
        long totalTime = endTime - startTime; // Calculate total runtime
        System.out.println("Total runtime (in milliseconds): " + totalTime / 1000000);
    }

    public static int checkFactors_5(BigInteger p) {
        int localCount = 0;
        int arraySize = 10000;
        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (5) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i5_1 = new int[]{6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0};
        int[] i5_3 = new int[]{6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0};
        int[] i5_7 = new int[]{6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0};
        int[] i5_9 = new int[]{8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==9) {
            for (int j = 8; j < arraySize; j += (i5_9[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if (pInt%10==1) {
            for (int j = 6; j < arraySize; j += (i5_1[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }else if (pInt%10==3) {
            for (int j = 6; j < arraySize; j += (i5_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }else{
            for (int j = 6; j < arraySize; j += (i5_7[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        if (!foundFactor) {
            unfactoredNumbers.add(twoToPMinus1);
        }
        return localCount;
    }

    public static int checkFactors_13(BigInteger p) {
        int localCount = 0;
        int arraySize = 10000;

        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (13) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i13_7 = new int[]{6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0};
        int[] i13_9 = new int[]{22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0};
        int[] i13_1 = new int[]{6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0};
        int[] i13_3 = new int[]{6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0};
        boolean foundFactor = false;
        if (pInt%10==3) {
            for (int j = 6; j < arraySize; j += (i13_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        } else if (pInt%10==1) {
            for (int j = 6; j < arraySize; j += (i13_1[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if(pInt%10==9) {
            for (int j = 22; j < arraySize; j += (i13_9[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        } else{
            for (int j = 6; j < arraySize; j += (i13_7[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        if (!foundFactor) {
            unfactoredNumbers.add(twoToPMinus1);
        }
        return localCount;
    }

    public static int checkFactors_7(BigInteger p) {
        int localCount = 0;
        int arraySize = 10000;

        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (7) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i7_7 = new int[]{10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0};
        int[] i7_9 = new int[]{10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0};
        int[] i7_1 = new int[]{10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0};
        int[] i7_3 = new int[]{10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==7) {
            for (int j = 10; j < arraySize; j += (i7_7[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if (pInt%10==9) {
            for (int j = 10; j < arraySize; j += (i7_9[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if (pInt%10==1) {
            for (int j = 10; j < arraySize; j += (i7_1[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else
            for (int j = 10; j < arraySize; j += (i7_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        if (!foundFactor) {
            unfactoredNumbers.add(twoToPMinus1);
        }
        return localCount;
    }

    public static int checkFactors_11(BigInteger p) {
        int localCount = 0;
        int arraySize = 10000;
        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (11) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i11_9 = new int[]{2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0};
        int[] i11_7 = new int[]{8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0};
        int[] i11_1 = new int[]{2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0,2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0};
        int[] i11_3 = new int[]{2,0,22,0,0,0,6,0,0,0,0,0,2,0,6,0,0,0,0,0,10,0,0,0,2,0,6,0,0,0,6,0,10,0,0,0,8,0,0,0,0,0,8,0,10,0,0,0,0,0,6,0,0,0,6,0,10,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==7) {
            for (int j = 8; j < arraySize; j += (i11_7[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if (pInt%10==9) {
            for (int j = 2; j < arraySize; j += (i11_9[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else if (pInt%10==1) {
            for (int j = 2; j < arraySize; j += (i11_1[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }
        else
            for (int j = 2; j < arraySize; j += (i11_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        if (!foundFactor) {
            unfactoredNumbers.add(twoToPMinus1);
        }
        return localCount;
    }

    private static ArrayList<BigInteger> generateExponentList1(int numberOfTerms) {
        ArrayList<BigInteger> exponentList1 = new ArrayList<>();

        for (int n = 0; n < numberOfTerms; n++) {
            int exponent1a = 5 + 12 * n;
            if (isPrime(exponent1a)) {
                exponentList1.add(BigInteger.valueOf(exponent1a));
                countTotal++;
            }
        }
        return exponentList1;
    }
    private static ArrayList<BigInteger> generateExponentList2(int numberOfTerms) {
        ArrayList<BigInteger> exponentList2 = new ArrayList<>();

        for (int n = 0; n < numberOfTerms; n++) {
            int exponent2a = 7 + 12 * n;
            if (isPrime(exponent2a)){
                exponentList2.add(BigInteger.valueOf(exponent2a));
                countTotal++;
            }
        }
        return exponentList2;
    }
    private static ArrayList<BigInteger> generateExponentList3(int numberOfTerms) {
        ArrayList<BigInteger> exponentList3 = new ArrayList<>();

        for (int n = 0; n < numberOfTerms; n++) {
            int exponent3a = 11 + 12 * n; //this could start at 107 for avoiding unnecessary calculations
            if (isPrime(exponent3a)) {
                exponentList3.add(BigInteger.valueOf(exponent3a));
                countTotal++;
            }
        }
        return exponentList3;
    }
    private static ArrayList<BigInteger> generateExponentList4(int numberOfTerms) {
        ArrayList<BigInteger> exponentList4 = new ArrayList<>();

        for (int n = 0; n < numberOfTerms; n++) {
            int exponent4a = 13 + 12 * n;
            if (isPrime(exponent4a)){
                exponentList4.add(BigInteger.valueOf(exponent4a));
                countTotal++;
            }
        }
        return exponentList4;
    }

    private static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    private static final HashMap<Integer, Integer> MODULO_MAP = new HashMap<>();

    static {
        MODULO_MAP.put(0, 0);
        MODULO_MAP.put(1, 1);
        MODULO_MAP.put(3, 2);
        MODULO_MAP.put(5, 3);
        MODULO_MAP.put(6, 4);
        MODULO_MAP.put(8, 5);
        MODULO_MAP.put(9, 6);
        MODULO_MAP.put(10, 6);
        MODULO_MAP.put(11, 7);
        MODULO_MAP.put(13, 8);
        MODULO_MAP.put(15, 9);
        MODULO_MAP.put(16, 10);
        MODULO_MAP.put(18, 11);
        MODULO_MAP.put(19, 12);
        MODULO_MAP.put(20, 12);
        MODULO_MAP.put(21, 13);
        MODULO_MAP.put(23, 14);
        MODULO_MAP.put(25, 15);
        MODULO_MAP.put(26, 16);
        MODULO_MAP.put(28, 17);
        MODULO_MAP.put(29, 18);
        MODULO_MAP.put(30, 18);
        MODULO_MAP.put(31, 19);
        MODULO_MAP.put(33, 20);
        MODULO_MAP.put(35, 21);
        MODULO_MAP.put(36, 22);
        MODULO_MAP.put(38, 23);
        MODULO_MAP.put(39, 24);
        MODULO_MAP.put(40, 24);
        MODULO_MAP.put(41, 25);
        MODULO_MAP.put(43, 26);
        MODULO_MAP.put(45, 27);
        MODULO_MAP.put(46, 28);
        MODULO_MAP.put(48, 29);
        MODULO_MAP.put(50, 30);
        MODULO_MAP.put(51, 31);
        MODULO_MAP.put(53, 32);
        MODULO_MAP.put(55, 33);
        MODULO_MAP.put(56, 34);
        MODULO_MAP.put(58, 35);
        MODULO_MAP.put(59, 36);
        MODULO_MAP.put(60, 36);
        MODULO_MAP.put(61, 37);
        MODULO_MAP.put(63, 38);
        MODULO_MAP.put(65, 39);
        MODULO_MAP.put(66, 40);
        MODULO_MAP.put(68, 41);
        MODULO_MAP.put(69, 42);
        MODULO_MAP.put(70, 42);
        MODULO_MAP.put(71, 43);
        MODULO_MAP.put(73, 44);
        MODULO_MAP.put(75, 45);
        MODULO_MAP.put(76, 46);
        MODULO_MAP.put(78, 47);
        MODULO_MAP.put(79, 48);
        MODULO_MAP.put(80, 48);
        MODULO_MAP.put(81, 49);
        MODULO_MAP.put(83, 50);
        MODULO_MAP.put(85, 51);
        MODULO_MAP.put(86, 52);
        MODULO_MAP.put(88, 53);
        MODULO_MAP.put(89, 54);
        MODULO_MAP.put(90, 54);
        MODULO_MAP.put(91, 55);
        MODULO_MAP.put(93, 56);
        MODULO_MAP.put(95, 57);
        MODULO_MAP.put(96, 58);
        MODULO_MAP.put(98, 59);
        MODULO_MAP.put(100, 60);
        MODULO_MAP.put(116, 61);
    }

    public static int modulo60(int number) {
        double decimalPart = ((double) number / 60) - (long) (number / 60);
        int firstThreeDigits = (int) (decimalPart * 100);
        // System.out.println(firstThreeDigits);
        return MODULO_MAP.getOrDefault(firstThreeDigits, -1);
    }

    public static boolean isFactorable(BigInteger value, BigInteger twoToPMinus1) {
        BigInteger remainder = twoToPMinus1.remainder(value);
        return remainder.equals(BigInteger.ZERO) && !value.equals(twoToPMinus1);
    }

    private static List<BigInteger> filterPrimes(List<BigInteger> numberList1) {
        List<BigInteger> primeNumberList = new ArrayList<>();

        for (BigInteger number : numberList1) {
            if (probablePrime(number)) {
                primeNumberList.add(number);
            }
        }

        return primeNumberList;
    }

    private static boolean probablePrime(BigInteger value) {
        if(value.isProbablePrime(10)){
            System.out.println(value);
            return true;
        }
        return false;
    }
    private static void printList(List<BigInteger> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Term " + (i + 1) + ": " + list.get(i));
        }
    }
}
