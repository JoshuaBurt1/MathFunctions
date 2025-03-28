package com.example.mathfunctions;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Checks for low factors. Then use probabilistic isProbablePrime() method to check higher numbers. --> balancing factorization time : isProbablePrime() time
// an array size of 1 would still be faster than purely probabilistic prime detection
// time                     primes       numberOfTerms     low factor array size
//9950ms/0.165 minutes (2+21 primes);    1000 iterations    arraySize = 90000;
//232981ms/3.88 minutes (2+24 primes);   2000 iterations    arraySize = 500000;
//3292485ms/54.87 minutes (2+25 primes); 4000 iterations    arraySize = 1000000;

public class MersennePrime {
    static int countTotal = 0;
    static ArrayList<BigInteger> unfactoredExponents = new ArrayList<>();

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        ArrayList<BigInteger> exponentList1 = generateExponentList1(1000);
        ArrayList<BigInteger> exponentList2 = generateExponentList2(1000);
        ArrayList<BigInteger> exponentList3 = generateExponentList3(1000);
        ArrayList<BigInteger> exponentList4 = generateExponentList4(1000);

        AtomicInteger countFactor = new AtomicInteger(0);
        long lowFactorTimeStart = System.nanoTime();
        // Parallelize the operations for each list
        exponentList1.parallelStream().forEach(p -> {
            countFactor.addAndGet(checkFactors_5(p));
        });
        System.out.println("5 origin factorization complete.");
        exponentList2.parallelStream().forEach(p -> {
            countFactor.addAndGet(checkFactors_7(p));
        });
        System.out.println("7 origin factorization complete.");
        exponentList3.parallelStream().forEach(p -> {
            countFactor.addAndGet(checkFactors_11(p));
        });
        System.out.println("11 origin factorization complete.");
        exponentList4.parallelStream().forEach(p -> {
            countFactor.addAndGet(checkFactors_13(p));
        });
        System.out.println("13 origin factorization complete.");
        System.out.println("Total with at least one 'low' factor: " + countFactor + "/" +countTotal);
        long lowFactorTimeEnd = System.nanoTime(); // Capture end time
        long totalLowFactorTime = lowFactorTimeEnd - lowFactorTimeStart; // Calculate total runtime
        System.out.println("Low factor search runtime (in milliseconds): " + totalLowFactorTime / 1000000);
        /*System.out.println("List of unfactored numbers");
        for (int i =0; i<unfactoredExponents.size();i++){
            System.out.print(unfactoredExponents.get(i) +"\n");
        }*/

        System.out.println("\nPrime Number List:");
        List<BigInteger> primeNumberList = filterPrimes(unfactoredExponents);
        printList(primeNumberList);

        long endTime = System.nanoTime(); // Capture end time
        long totalTime = endTime - startTime; // Calculate total runtime
        System.out.println("Total runtime (in milliseconds): " + totalTime / 1000000);
    }

    public static int checkFactors_5(BigInteger p) {
        int localCount = 0;
        int arraySize = 90000;
        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (5) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i5_1 = new int[]{5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0};
        int[] i5_9 = new int[]{7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0};
        int[] i5_3 = new int[]{5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0};
        int[] i5_7 = new int[]{5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==9) {
            for (int j = 8; j < arraySize; j += 1 + (i5_9[modulo60(j)])) {
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
            for (int j = 6; j < arraySize; j += 1 + (i5_1[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }else if (pInt%10==3) {
            for (int j = 6; j < arraySize; j += 1 + (i5_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 5, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        }else{
            for (int j = 6; j < arraySize; j += 1 + (i5_7[modulo60(j)])) {
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
            unfactoredExponents.add(p);
        }
        return localCount;
    }

    public static int checkFactors_13(BigInteger p) {
        int localCount = 0;
        int arraySize = 90000;

        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (13) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i13_7 = new int[]{5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0};
        int[] i13_9 = new int[]{21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0};
        int[] i13_1 = new int[]{5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0};
        int[] i13_3 = new int[]{5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0};
        boolean foundFactor = false;
        if (pInt%10==3) {
            for (int j = 6; j < arraySize; j += 1 + (i13_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        } else if (pInt%10==1) {
            for (int j = 6; j < arraySize; j += 1 + (i13_1[modulo60(j)])) {
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
            for (int j = 22; j < arraySize; j += 1 + (i13_9[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 13, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        } else{
            for (int j = 6; j < arraySize; j += 1 + (i13_7[modulo60(j)])) {
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
            unfactoredExponents.add(p);

        }
        return localCount;
    }

    public static int checkFactors_7(BigInteger p) {
        int localCount = 0;
        int arraySize = 90000;

        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (7) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i7_7 = new int[]{9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0};
        int[] i7_9 = new int[]{9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0};
        int[] i7_1 = new int[]{9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0};
        int[] i7_3 = new int[]{9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==7) {
            for (int j = 10; j < arraySize; j += 1 + (i7_7[modulo60(j)])) {
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
            for (int j = 10; j < arraySize; j += 1 + (i7_9[modulo60(j)])) {
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
            for (int j = 10; j < arraySize; j += 1 + (i7_1[modulo60(j)])) {
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
            for (int j = 10; j < arraySize; j += 1 + (i7_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        if (!foundFactor) {
            unfactoredExponents.add(p);
        }
        return localCount;
    }

    public static int checkFactors_11(BigInteger p) {
        int localCount = 0;
        int arraySize = 90000;
        BigInteger twoToPMinus1 = BigInteger.TWO.pow(p.intValue()).subtract(BigInteger.ONE);
        //System.out.println("M#: 2^" + p + "-1 = " + twoToPMinus1);
        int pInt = p.intValue();
        //these arrays are specific to the generating number (11) & the ending number - to not hit numbers divisible by 3 & 5 and must be binary factor ending in 1001,0001,1111,0111
        int[] i11_9 = new int[]{1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0};
        int[] i11_7 = new int[]{7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0};
        int[] i11_1 = new int[]{1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0,1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0};
        int[] i11_3 = new int[]{1,0,21,0,0,0,5,0,0,0,0,0,1,0,5,0,0,0,0,0,9,0,0,0,1,0,5,0,0,0,5,0,9,0,0,0,7,0,0,0,0,0,7,0,9,0,0,0,0,0,5,0,0,0,5,0,9,0,0,0};
        boolean foundFactor = false;

        if (pInt%10==7) {
            for (int j = 8; j < arraySize; j += 1 + (i11_7[modulo60(j)])) {
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
            for (int j = 2; j < arraySize; j += 1 + (i11_9[modulo60(j)])) {
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
            for (int j = 2; j < arraySize; j += 1 + (i11_1[modulo60(j)])) {
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
            for (int j = 2; j < arraySize; j += 1 + (i11_3[modulo60(j)])) {
                BigInteger test = (p).multiply(BigInteger.valueOf(j)).add(BigInteger.ONE);
                if (isFactorable(test, twoToPMinus1)) {
                    //System.out.println("Origin 7, factor found: " + test + " @ multiple " + j);
                    localCount++;
                    foundFactor = true;
                    break;
                }
            }
        if (!foundFactor) {
            unfactoredExponents.add(p);
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

    //low certainty (1) okay as other requirements must be met
    public static boolean isFactorable(BigInteger value, BigInteger twoToPMinus1) {
        return value.isProbablePrime(1) && (twoToPMinus1.remainder(value).equals(BigInteger.ZERO) && !value.equals(twoToPMinus1));
    }

    private static List<BigInteger> filterPrimes(List<BigInteger> numberList1) {
        System.setProperty("java.util.concurrent.ForkJoinPool.commonPool.parallelism", "4");
        return numberList1.parallelStream() // create a parallel stream
                .filter(number -> {
                    // If the number is greater than 18000, use Lucas Lehmer test (generally faster for higher exponents)
                    if (number.compareTo(BigInteger.valueOf(16383)) > 0) {
                        return lucasLehmerTest(number); // Use Lucas Lehmer test
                    } else {
                        return millerRabinCustom(number, 3); // Use probable prime test with base 3
                    }
                })
                .collect(Collectors.toList());
    }


    // Miller-Rabin Primality test using a specific base (3)
    private static boolean millerRabinCustom(BigInteger exponent, int base) {
        // Compute value-1, used in the test (Example: value = 31; d = 30)
        BigInteger value = BigInteger.ONE.shiftLeft(exponent.intValue()).subtract(BigInteger.ONE);

        BigInteger d = value.subtract(BigInteger.ONE);
        int s = 0;
        // Decompose value - 1 as d * 2^s (Example: d = 30 mod 2 -> d = 15)
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.shiftRight(1);
            s++;
        }
        // Compute a^d mod value
        BigInteger a = BigInteger.valueOf(base);
        BigInteger x = a.modPow(d, value);
        // Check if the result is 1 or value-1 (witness for primality)
        if (x.equals(BigInteger.ONE) || x.equals(value.subtract(BigInteger.ONE))) {
            System.out.println(value);
            return true;
        }
        //System.out.println("Composite: " + value);
        return false;
    }

    // Lucas-Lehmer Primality test (designed for Mersenne Primes, but works well for large exponents)
    private static boolean lucasLehmerTest(BigInteger exponent) {
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
            return true;
        } else {
            return false;
        }
    }

    private static void printList(List<BigInteger> list) {
        // Loop through the sorted list and print each BigInteger in 2^p - 1 form
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            BigInteger value = BigInteger.ONE.shiftLeft(list.get(i).intValue()).subtract(BigInteger.ONE);
            // Print the term number and the Mersenne number representation
            System.out.println("Term " + (i + 1) + ": " + value);
        }
    }
}




/*
Notes: Attempting to get a general idea of what a solution should look like:
|\
| \
|  \
|___\
~Given a value: 2^p-1
~A factor choice (line) from 2*p+1 to sqrt(2^p-1) exists going linearly with the formula 2^1*p+1  <-> 2^1*p+...+2^n*p...+1
~some are close to dividing 2^p-1
~base folds are 11(3) & 111(7)

######################
Good potential algorithm B: LOOK FOR LONG 0 STRINGS (00000...)
# M59 -> 2^59-1 = 576460752303423487 = 179951*3203431780337 = (59*3050+1) * (59*54295453904+1)
((2^59-1)-1)/59 = 9770521225481754
((2^59-1)-1)/59 = 2*3*(233*1103*2089)*3033169 = 9770521225481754
M29 = (233*1103*2089)
29*2 = 58 -> 1 less than 59

59*3050+1 = 179951 (rem 0)
3050 = 3*3*7*7*7-(3+3+3+7+7+7+7)
3050 = 2*5*5*61

Algo: Search: 000... factor string -> guess factors
There are 161 groups of p (59) + value 1. Factor = 9500 remainder: 487 Resulting in : 60680079189834 divisions of 576460752303423487    #just guess a multiple of 61
There are 661 groups of p (59) + value 1. Factor = 39000 remainder: 487 Resulting in : 14781044930857 divisions of 576460752303423487
There are 61 groups of p (59) + value 1. Factor = 3600 remainder: 3487 Resulting in : 160127986750950 divisions of 576460752303423487    #"3487" 360 degrees * 10 @ 61
There are 2554 groups of p (59) + value 1. Factor = 150687 remainder: 3487 Resulting in : 3825550660000 divisions of 576460752303423487
"61,2,5,5,4 affinity to 0"
"iterable guessing at 61 -> goto 3050 = 2*5*5*61"
There are 261 groups of p (59) + value 1. Factor = 15400 remainder: 2887 Resulting in : 37432516383339 divisions of 576460752303423487
There are 1261 groups of p (59) + value 1. Factor = 74400 remainder: 3487 Resulting in : 7748128391175 divisions of 576460752303423487
"2,61, ..."
*Note, this will give a good guess but it is slow (general algorithm is 21 steps, this is at least 61)

Algo: Search significance values from analysisTool
Factor: 2833, division: 203480675010032, remainder: 2831 (2), significance: 1416, Number: 576460752303423487 #Step 1
Factor: 8497, division: 67842856573310, remainder: 80, significance: 106, Number: 576460752303423487
Factor: 11329, division: 50883639536007, remainder: 184, significance: 61, Number: 576460752303423487
"create an iterative guess thread: 61.... 61*2*5*5 vs 21 steps"
# VISUAL
"2831 = 10](110000)[1111   BIN" actual remainder, 2 short
       {101}{10}00{1}00{01}
"2833 = 101100010001       BIN"
(2833-1)/59 = 48 = (110000)
"61   =        [111101]    BIN"




#########

>(-.o)>

M#59 -> 2^59-1 = 576460752303423487 = 179951*3203431780337 = (59*3050+1) * (59*54295453904+1)
59*(3050)+1 = 179951
59*(61*5*5*2)+1 = 179951

// LUCAS-LEHMER vs MILLER-RABIN primality test relation for Mersenne numbers (2^p-1)
~2^[59]-1 has a strong gematria relation to 2^16383-1 = 2^((2^14)-1)-1 = [5.9]4865*10^4931 :
i. 2^59-1 is one of the first numbers with a more difficult factor to find (use case for primality tests)
ii. p = 16383 (last 14 digit binary power) is generally where the Lucas-Lehmer primality test becomes faster than Miller-Rabin primality test
iii. it relates to the first two digits of the # where the calculation times cross at: 2^16383-1 = [5.9]4865e+4931 (Lucas-Lehmer is now faster than Miller-Rabin test)
The sequence (first 3) of possible prime factors for 2^59-1:
Potential Factor: 59*2^1+59*2^4+1 =  1063 -> starts with binary powers similar to 2^((2^14)-1)-1:
Potential Factor: 59*2^5+1        =  1889
Potential Factor: 59*2^5+59*2^4+1 =  2833 -> ends with all possible base 10 numbers in the matrix: 16383 (without repeating)
...

// Miller-Rabin Algorithm: https://www.youtube.com/watch?v=_MscGSN5J6o
a= 23 "witness", n = 747 "is this prime?"
n= 747 = 2^m*d+1
        = 2^1*373+1
a^d=1 MOD n?
23^373 MOD 747 = 131 (not 1, therefore not prime)
// For better results use a combination of witness numbers "judging whether prime or composite"
a=2,3             -> works up to 1373653   -> it will be prime if 2 and 3 say it is prime. Above this it is still a high chance that if 2 and 3 say it is prime, it will still be prime.
a=2,3,5           -> works up to 25326001
a=31,73           -> works up to 9080191
a=2,13,23,1662803 -> works up to 1122004669633
a=2,3,5,7,11      -> works up to 2152302898747
a=2,3,5,7,11,13,17,19,23,29,31,37 -> works up to 318,665,857,834,031,151,167,461... ~10^23
//However, Lucas-Lehmer will determine 2^n-1 primality faster past 2^16383-1 compared to Miller-Rabin


//A. Sargon II: early Mesopotamian gematria (713 BCE) : https://www.ucl.ac.uk/sargon/downloads/frahm_nabu2005_44.pdf
//"I made the circumference (lit., measure) of its (the city's) wall 16283 cubits, (corresponding to) my name (nibīt šumīya)."
// Circumference = 16283. Radius = 16283÷(2*π) = [2591].5199 -> [2^59-1]
       (or a = 2,3 witness #s)           12 3 45
// "My (23:human chromosome pair) name": 16[2]83   -> switch 3rd digit from 2 to 3 -> 16383
                          add remainder:
                       1+1,2+6,4+8,5+3 = 2,8, 12,8 -> match to [2^59-1] possible factor sequence

The sequence (first 3) of possible prime factors for [2^59-1]:
Potential Factor: 59*2^1+59*2^4+1 =  10|63
Potential Factor: 59*2^5+1        =  18|89
Potential Factor: 59*2^4+59*2^5+1 =  28|33  -> add separation (|): 10+63+18+89+28+33 = 241 & (|) -> 2^14-1 = 16383
// left side: 10(bin)+=2+8+1+2+8  =  23
// right side: 6+3+8+9+3+3        =     32  ->  either way: 23 human chromosome pairs
// top and bottom separation & left and right separation -> cross of the Lucas-Lehmer and Miller-Rabin primality test calculation times at p = 16383


//B. HEBREW TEXTS : https://mechon-mamre.org/p/pt/pt1020.htm
//i. Isaiah 20:1
א  בִּשְׁנַת בֹּא תַרְתָּן, אַשְׁדּוֹדָה, בִּשְׁלֹחַ אֹתוֹ, סַרְגוֹן מֶלֶךְ אַשּׁוּר; וַיִּלָּחֶם בְּאַשְׁדּוֹד, וַיִּלְכְּדָהּ.
"In the year that Tartan came into Ashdod, when Sargon the king of Assyria sent him, and he fought against Ashdod and took it"
1+(2+300+50+400)+(2+1)+(400+200+400+50)+(1+300+4+6+4+5)+(2+300+30+8)+(1+400+6)+(60+200+3+6+50)+(40+30+20)+(1+300+6+200)+(6+10+30+8+40)+(2+1+300+4+6+4)+(6+10+30+20+4+5)
= [1+752+3]+1050+320+340+407+319+90+507+94+[317+75]
* Knowing Sargon "king:#1" is associated with 23 & note 1st 3 digits: 1+(2+3...: split into 2 and 3 groups and choose greatest digit:
// Choose greatest digit: [1,7,3],[3,7],(2,[groups])  & remainder: 1,3,3,4,3,9,{5,9} -> note last 2 are 59 -> 2^59
// Rearrange: 3021377 & 1+3+3+4+3+9+5+9 = 37 -> 37th Mersenne Prime : p = 3021377 -> very difficult to find without using Lucas-Lehmer or Miller-Rabin primality tests
// Mersenne # sequence: 11 (3), 111 (7)
// Note: camel: גמל = 73 -> -> continues to Midrash on the Song of Songs, Bible, and Quran
// Note: elephant: 120 = פיל -> continues to Baba Metzia, 38b

//ii. Midrash on the Song of Songs
//"The Holy One said, open for me a door as big as a needle's eye and I will open for you a door through which may enter tents and camels."
Miller-Rabin & Lucas-Lehmer test cross point: p=16383 & remainder #s
Potential Factor: 59*2^1+59*2^4+1 = [10 6 3] -> Hebrew ו vav (6:tent peg) in shape and order of 16383; camel hump shape : 1 sigma : 68%
Potential Factor: 59*2^5+1        =  18[8]9  -> 1*8*2*8*9*3 = 3456 -> {345 : "פומבדיתא"} & 6 -> Hebrew ו vav in shape and order of 16383
Potential Factor: 59*2^5+59*2^4+1 =  28[3]3     18*28+39 = {543 : פומבדיתא} : opposite 18+28*39 = 1110 (14 base 2) -> 2^((2^14)-1)-1

//iii. Baba Metzia, 38b
//"Are you from Pumbedita (Pythagorean triple: {543} = פומבדיתא), where they push an elephant (80+10+30 = 120 = פיל) through the eye of a needle?".
//                                                                                   1 2 345  -> 543 = פומבדיתא
// Baba Metzia, 38b, elephant (80+10+30=120) is in place of the usual camel (68%) -> 1[8]383 -> switch 2nd digit -> 16383
//                                                                                     6  -> can’t have a 2 without a 6 ->”Yahweh” IHVH = 26
//                                                                                     -> vertical 2nd col : 1 sigma : 68.2% ("camel hump shape")
//                                                                                   -> vertical 1st col: 11  first prime of the M# sequence


//C. BIBLE - SEPTUIGANT (ANCIENT GREEK): https://www.blueletterbible.org/mgnt/mat/19/24/s_948024
"Again I tell you, it is easier for a camel to go through the eye of a needle than for someone who is rich to enter the kingdom of God.”
//Assuming this is retained from a Hebrew source -> Miller-Rabin & Lucas-Lehmer test cross point: p=16383
Potential Factor: 1063 -> [10 6 3] -> Hebrew ו vav (6) in shape and order of [16383];
Potential Factor: 1889 ->  18[8]9
Potential Factor: 2833 ->  28[3]3

Matthew 19:24 -> [19:2 4]        -> [19+24+55+74+47] = 219 κάμηλον : camel (A. Greek) in the shape of Hebrew ב bet (2)
Mark    10:25 ->  10:2[5]        -> Isaiah: 20:1 reference
Luke    18:25 ->  18:2[5]        -> total remainder from ב bet : 128^2-1 = 16383
Sum     47:74 -> [47:7 4]        -> 74: camel chromosome number
                                 Note: This completes IHVH = 26 (Hebrew) :ב|ו ("God the Father / Yahweh")

// Whoever wrote this appears to know about factor digits having a higher distribution with the constant e & around 0s; see ADDENDUM: OTHER ANCIENT GREEK
// (4) corners of Matthew, Mark, Luke, Sum: 1+4+4+4+(4) = 17
                                 0 1  2  3  4
// The centered-hexagon sequence 1,7,19,37,61... relation to e = ~ (61/37)^2, (91/61)^2.5, (127/91)^3, ...
Mersenne Prime 37 is retained from Isaiah: 20:1 between sum values 47 and 74 in a centered-hexagon number structure
           ...
       37, 55, 79, 109, 145, 187    // Up to the 37th "step", under 79 : 1 "prime"
       36, 54, 78, 108, 144, 186    // Highlight sum values: [47],[74] & bet remainder values: [102],[182]
       35, 53, 77, 107, 143, 185
       34, 52, 76, 106, 142, 184
       33, 51, 75, 105, 141, 183
       32, 50,[74],104, 140,[182]
    19,31, 49, 73, 103, 139, 181    // Between [47] & [74] -> 73,72,71  in a G:Γ = 3 shape
    18,30, 48, 72,[102],138, 180    //                         3_21377  (extra 7) -> Mp 37 = 3021377
    17,29,[47],71, 101, 137, 179    // 3 above 2 in [102] : Sargon II inscription & Isaiah: 20:1 (only mention of Sargon) reference
    16,28, 46, 70, 100, 136, 178                                         012    102 : Isaiah: 20:1
    15,27, 45, 69,  99, 135, 177    // Diagonally between [102], [182] : 139 -> 319 = Sargon: סַרְגוֹן
    14,26, 44, 68,  98, 134, 176    // Between 74, 102 and 182 -> 34.1[11]4 : 1 sigma 1 side
  7,13,25, 43, 67,  97, 133, 175
  6,12.24. 42, 66,  96, 132, 174
  5,11,23, 41, 65,  95, 131, 173
  4,10,22, 40, 64,  94, 130, 172
  3, 9,21, 39, 63,  93, 129, 171
1,2, 8,20, 38, 62,  92, 128, 170

// Reason for the new numbers? The Hebrew version (Isaiah: 20:1) is more compact.
//Hints:
i.  4 entries in ב bet (2)}
ii. {2 or 4} words: eye of the needle                               Highlight Hebrew ו vav (6) matrix #s
Matthew 19:24: τρυπήματος ῥαφίδος (truphmatos rafidos)             = [2 3 8]4  -> Remainder #s: 4,2,4,7,5
Mark: 10:25: τῆς τρυμαλιᾶς τῆς ῥαφίδος (ths trumalias ths rafidos) =  2[9 8 3]                  4*2*4*7*5 = 1120
Luke 18:25: τρήματος βελόνης (trhmatos belonhs)                   = [1 3 8]4                   4+2+4+7+5 = 22
Sum:                                                                [6]7 5[1]                              1120+22= 11,{42}

Matrix 2 & 6 remainder: 1+8+9+2+8+3+1+0+2+1+8+2 = 45
                        1*8*9*2*8*3*1*2*1*8*2 = 110592
Matrix 2 & 6 letters:   1+0+6+3+8+3+1+9+2+4+5+5+4+7+7+4 = 69        -> addition difference: 69-45 = {24}
                        1*6*3*8*3*1*9*2*4*5*5*4*7*7*4 = [609638]400 -> similar #s to vav (6) matrix, highlight first 6; ד dalet (4) remains
//Highlight Hebrew ו vav (6) matrix; note: 110(bin) = 6
[1 0 63]  -> 1638(9=3^2) & 1 -> rearrange: 2^16383-1 (Miller-Rabin & Lucas-Lehmer test cross point)
[1]8[89]
 2 8 33  -> mirrored Hebrew ד dalet (4): door
// Mirrored dalet #s: 2+8+8+3+3 = 24 : {42} mirrored
// All other #s rearranged           : 25964951 & 10=2 or 01=1 -> 2^25964951-1
// MP {42}: p=25964951 -> very difficult to find without using Lucas-Lehmer or Miller-Rabin primality tests


//D. QURAN (QURANIC ARABIC): https://www.masjidtucson.org/quran/wordCount/QuranGV.php
"Surely the gates of Heaven shall not be opened for those who reject Our signs as false and turn away from them in arrogance
nor shall they enter Paradise until a camel passes through the eye of a needle. Thus do We reward the guilty ones."
// Quran Verse 7:40         -> 74: camel chromosome number
// Num letters: 102         -> Isaiah: 20:1 reference
// Gematrical Value: 7447   -> 74: camel chromosome number, permutation of the Bible sum
// (2^59-1) factor: 179951
// 7447*102 =       759594 -> match:7995, no match:54 & 11=[3 bin] -> concatenate binary {543} (Pumbedita & Pythagorean triple: {543} = פומבדיתא)
// 7447/102 = [73].0098039 -> camel: גמל = 73 -> rearrange: [79]9383 -> gold equivalency [1]9383 -> remember gold? 196amu. Switch 2nd # -> 16383

           ...
       37, 55, 79, 109, 145, 187
       36, 54, 78, 108, 144, 186
       35, 53, 77, 107, 143, 185
       34, 52, 76, 106, 142, 184
       33, 51, 75, 105, 141, 183
       32, 50,[74],104, 140, 182   // Between [74] & [47] crossing [102] -> 104,103,102,101  in an A:alif (أ) = 1 shape
    19,31, 49, 73, 103, 139, 181   //                                       43112(110)(0)(1001)
    18,30, 48, 72,[102],138, 180   //                            Mp 47: p = 43112609
    17,29,[47],71, 101, 137, 179
    16,28, 46, 70, 100, 136, 178
    15,27, 45, 69,  99, 135, 177
    14,26, 44, 68,  98, 134, 176
  7,13,25, 43, 67,  97, 133, 175
  6,12.24. 42, 66,  96, 132, 174
  5,11,23, 41, 65,  95, 131, 173
  4,10,22, 40, 64,  94, 130, 172
  3, 9,21, 39, 63,  93, 129, 171
1,2, 8,20, 38, 62,  92, 128, 170

// Reason for the new numbers?
  X
//2 words: eye of a needle = سَمِّ الخِياط
 XX X     X X         X X
(60,40); (1,30,600,10,1,9);                                  012 -> 201 : Isaiah: 20:1 reference
         43112609  -> Extra #s: 10,600,00 -> 6*100000(bin) = 192 -> 219 : 219 κάμηλον : camel (A. Greek) & 1+6 = 7 (centered-hexagon sequence; below)
MP 47: p=43112609  -> very difficult to find without using Lucas-Lehmer or Miller-Rabin primality tests

//HEBREW TEXTS, BIBLE, & QURAN
~ "the eye of the needle" appears to relate to the calculation time curves of Lucas-Lehmer vs. Miller-Rabin primality test (they cross at p=16383)
~ https://en.wikipedia.org/wiki/Pumbedita

~"the camel" combined with "heaven:kingdom of god" -> going to a high number:
i. 11 (3), 111 (7) -> start of the Mersenne # sequence : camel has 37 pairs of chromosomes -> paired with "eye of the needle" p = 11111111111111 = 2^14-1 = 16383
ii. camel hump probability shape : 1 sigma = 68%; instead of actually finding a factor, Lucas-Lehmer and Miller-Rabin are saying if a # is probably composite or prime
Aside: [L]ucas-[L]ehmer and [M]iller-[R]abin: L:30,L:30,M:40,R:100 (A. Greek) -> 34.13 1 sigma 1 side


//E. BHAGAVATA PURANA (Śrīmad-Bhāgavatam) : https://vedabase.io/en/search/?query=elephants
SEARCH "ELEPHANT" -> elephant trunk : camel hump shape (1 sigma: 68%)
ŚB 10.36
                           Match:      34      43 -> (elephant trunk 34+34 = 68%: 1 sigma )
Potential Factor: 59*2^1+59*2^4+1 =  1063 -> 1036
Potential Factor: 59*2^5+1        =  1889
Potential Factor: 59*2^5+59*2^4+1 =  2833

ŚB 10.36.11
गृहीत्वा श‍ृङ्गयोस्तं वा अष्टादश पदानि स: । प्रत्यपोवाह भगवान् गज: प्रतिगजं यथा ॥ ११ ॥
The Supreme Lord Kṛṣṇa seized Ariṣṭāsura by the horns and threw him back eighteen steps, just as an elephant might do when fighting a rival elephant.
// All #s: 10=2, 11=3, 36, 18 -> 2^16383 -> Lucas-Lehmer or Miller-Rabin primality test cross point
ŚB 10.36.21:
The King of the Bhojas next called for his ministers, headed by Muṣṭika, Cāṇūra, Śala and Tośala, and also for his elephant-keepers.The King addressed them as follows.
ŚB 10.36.25
महामात्र त्वया भद्र रङ्गद्वार्युपनीयताम् । द्विप: कुवलयापीडो जहि तेन ममाहितौ ॥ २५ ॥
You, elephant-keeper, my good man, should position the elephant Kuvalayāpīḍa at the entrance to the wrestling arena and have him kill my two enemies.
ŚB 10.36.32:
After you bring Kṛṣṇa and Balarāma, I will have Them killed by my elephant, who is as powerful as death itself.
And if by chance They escape from him, I will have Them killed by my wrestlers, who are as strong as lightning.

//Chapter Analysis:
Canto 10
Verse sum of elephant mentions: 32+25+21+11 = 89 -> 10th Mp: p = 89

Reverse the elephant verses:       32252111  <- Last 4 digits
                                    [E]XXXX
Mp 10: 2^89-1 = 618970019642690137449562111
                           5
E = elephant chromosomes: 56 -> 90° : y=1

Entry to Mp 39:
                    XXXXXX2X     ~Notice similarity to Mp 10                              98765 4321 -> 9th value is off by 3 (Look up Mp 39 from a list...)
6594-5223= 1371 -> [13714956] (concatenate 1371|4956 : Mp 10: 2^89-1 = 618970019642690[13744956]2111
                    XXXX4XXX
Mp 39:            p=13466917 -> Only non-match is 5 & 6 : 56 elephant chromosomes
                                     3                           4 -> 34% (1 sigma 1 side)
1371496+1346917 = 2718,413 -> [First 3 digits of π (3.14), First 4 digits of e (2.718)]
// Used in standard normal distribution equation -> e^((-z^2)/2)/sqrt(2π) : "Elephant trunk :Camel hump shape"
// Was the above a random occurrence? Probably not.

//Mp 10 & 39 ->         23      32 : switch 3rd digit 2 : Sargon inscription
//Sargon II reference: 3910 -> 319 = Sargon: סַרְגוֹן
//13466917 -> 1+3+4+6+6+9+1+7 = 37 : Sargon is associated with the 37th Mp

//Unmatched values: 2:5 & 4:6 -> 2+5=7, 4+6=10 -> 01, 7 -> Check centered-hexagon structure (1,7,19,37,61...)
                    2:4 & 5:6 -> 2+4=6, 5+6=11 -> group 11 row 6 : gold
// Match story numeric value to centered-hexagon structure : https://vedabase.io/en/library/sb/10/36/
// humped-bull demon : कुकुद्मिनं (Kukudminam) = 2(Ka)+6(U)+2(Ka)+6(U)+4(Da)+4(M)+1(I)+5(Na)+4(M) = 34 (hump shape: 34% : 1 sigma) exists -> presumably Mp 39
// Kṛṣṇa (कृष्ण) = 2(Ka)+2(Rri)+5(Sh)+5(Na) = 14, helps the humans (23) -> locate {23, 41};  bull-demon tears up the embankments
// Kṛṣṇa (14) holds the bull demon by the horns (1) and throws it back 18 steps. Mp 39 is up diagonal: 1,66,97,134 -> 13466917
//                                                               From structure base to Mp 39 digits: 3+ 4+ 5+ 6 = 18 steps
             ...
          37, 55,   79, 109, 145, 187
          36, 54,   78, 108, 144, 186
          35, 53,   77, 107, 143, 185
          34, 52,   76, 106, 142, 184
          33, 51,   75, 105, 141, 183
         [32],50,   74, 104, 140, 182 // Between 32 and 25:
     19,  31, 49,   73, 103, 139, 181 // Sargon II #s: 31+30+29 = 90
     18,  30, 48,   72, 102, 138, 180 // Other #s:     28+27+26 = 81 -> Mp 10: p=89
     17,  29, 47,   71, 101, 137, 179
     16,  28, 46,   70, 100, 136, 178
     15,  27 ,45,   69,  99, 135, 177 // Mp 39 : diagonal (134)(97)(66)(1) -> p=13466917;
     14,  26, 44,   68,  98,(134),176 // Mp 39 relation: 39th prime is 167 -> per"mutation" of 176 next to the bull-"demon"
  7, 13, [25],43,   67, (97),133, 175 // Under [25] and skipping [21] : 24+23+22+20 = 89 -> Mp 10 p=89
  6, 12,  24, 42,  (66), 96, 132, 174
  5,[11],{23, 4(1)},65,  95, 131, 173 // Under [11]; 10 & 8,9 -> Mp 10 p=89
  4, 10,  22, 40,   64,  94, 130, 172
  3,  9, [21],39,   63,  93, 129, 171
1,2,  8,  20, 38,   62,  92, 128, 170

// MP {39}: p=13466917 -> very difficult to find without using Lucas-Lehmer or Miller-Rabin primality tests




//ADDENDUM:
//OTHER ANCIENT GREEK
* Possible Assyrian-Ancient Greek knowledge sharing:
* Assyria (713 BCE)           Ancient Greek sequence within 16383 -> Greek God Hermes (Ἑρμῆς) = 353 permutation (~ 700 BCE)
Radius = 16283÷(2×pi) = 2591.5199,3836[533]1...

* The Ancient Greeks possibly observe that the prime factors of the group are more common around a greater number of 0s:
i.
DIVIDING AGAINST e : relation to the Greek God, Hermes (Ἑρμῆς) = 353
# e-1 sequence (1.7182...) & e sequence (2.7182...)
~ possible relation to the tree (n!) structure of permutations, where n is the length of the # string
# M59 -> 2^59-1 = 576460752303423487 = 179951*3203431780337 = (59*3050+1) * (59*54295453904+1)
59*(3050)+1 = 59*(61*5*5*2)+1 = 179951
Potential Factor: 1063, division: 542296098121753, remainder: 48
Potential Factor: 1889, division: 305167153151627, remainder: 84
Potential Factor: 2833, division: 20348067501[00]32, remainder: 2831
203480675010032*(1/0!+1/1!+1/2!+1/3!+1/4!+1/5!+1/6!+1/7!+1/8!+1/9!+1/10!+1/11!+1/12!+1/13!+1/14!+1/15!+1/16!+1/17!+1/18!) = 553117821322[350.535]88   //Hermes:Mercury:Planet 88 days : 8*11=88;
// Strong myth relation: Hermes (Ἑρμῆς) = 353; turtle shell (hexagon pattern : e) myth (50 = row 8 col 1 cows from Apollo, makes a lyre:number string); factor is 179951 = 101011111011101111 : 18 digits
// The prime factors of the group seem to be more common around a greater number of 0s:
203480675010032*(1/1!+1/2!+1/3!) = 339134458[350053.3] ->   contains 5,5 of 61*5*5*2 = 3050 & Ἑρμῆς relation
203480675010032*e = 553117821322[350.53]               ->   contains 5,5 of 61*5*5*2 = 3050 & Ἑρμῆς relation
2833/e = 1042.2024568...6190367818[52500186]642        ->   contains 61,5,5,2 of 61*5*5*2 = 3050

ii.
ARTIFICIALLY MAKING 0's
M#: 2^59-1 = 576460752303423487 = 179951 * ...
Potential Factor: 1063, division: 542296098121753, remainder: 48
Potential Factor: 1889, division: 305167153151627, remainder: 84
Potential Factor: 2833, division: 20348067501[00]32, remainder: 2831
203480675010032-32 = 203480675010000 //gold = 1570 (A. Greek), next to a 68 (1 sigma)
32*2833 = 90656
90656/59 = 1536, 32 remainder -> contains 61,5,2 of 61*5*5*2 = 3050
remainders 2831+32 = 2863  // permutation of 2368 =Jesus Christ (A.Greek) in The Rich Man (who would artificially make 0s)
                           // Camel art near an opening 0: https://en.wikipedia.org/wiki/Eye_of_a_needle#/media/File:Dortmund,_Bonifatius-Kirche,_Eingang_West.jpg

#########









#M71    2^71-1 = 2361183241434822606847 = 228479 * ...
Factor: 228479 ...01111111, division: ...10000001, remainder: 0
(228479-1)/71 = 3218 groups
3218 = 2*1609

* Algo: Search: 000... string -> guess factors
There are 100 groups of p (71) + value 1. Factor = 7101 remainder: 6736 Resulting in : 332514186936322011 divisions of 2361183241434822606847
There are 169 groups of p (71) + value 1. Factor = 12000 remainder: 6847 Resulting in : 196765270119568550 divisions of 2361183241434822606847
There are 2000 groups of p (71) + value 1. Factor = 142001 remainder: 1011 Resulting in : 16627933897893836 divisions of 2361183241434822606847
"169 affinity to 0"
"iterable guessing at 169 (prime version 1609) -> go to 3218 = 1609*2"
There are 26169 groups of p (71) + value 1. Factor = 1858000 remainder: 2847 Resulting in : 1270819828544038 divisions of 2361183241434822606847
* Algo: Search: 777... string -> guess group
There are 537 groups of p (71) + value 1. Factor = 38128 remainder: 143 Resulting in : 61927802177791193 divisions of 2361183241434822606847
"34.1" -> 38128 -> 3218

# VISUAL
71 =           [100 01 11]
5113 = [10](1000100)00[00]
(5113-1)/71= 72 = (1001000)
(72-1)/71 = 1
1609 = [11001001001]


###########



71*3218+1 = 228479
71*(1609*2)+1 = 228479

ARTIFICIALLY MAKING 0's
M#: 2^71-1 = 2361183241434822606847 = 228479 * ...
Factor: 569, division: 4149706926950479098, remainder: 85, significance: 6, Number: 2361183241434822606847
Factor: 1279, division: 1846116686031917597, remainder: 284, significance: 4, Number: 2361183241434822606847
Factor: 1847, division: 1278388327793623501, remainder: 5[00], significance: 3, Number: 2361183241434822606847 // Step 3
1278388327793623501-23500 = 1278388327793600001
500*1847 =     923500
3500*1847 =   6464500
23500*1847 = 43404500
923500/71 is:    13007,
6464500/71 is:   91049,
43404500/71 is: 611330,

DIVIDING AGAINST e
3218*71+1 = 228479
(1609*2)*71+1 = 228479
1278388327793623501*(1/1!+1/2!) = 191758249[1690]435251.5
1278388327793623501*(1/0!+1/1!+1/2!+1/3!) = 340903554078[2996002].66
1847/e = 679.47332784365396798693240348821822217241315901567519033597457273521138264082993680065101571691358[2000020669]7
###########




# M167 -> 2^167-1 = 187072209578355573530071658587684226515959365500927 = 2349023 * 79638304766856507377778616296087448490695649
((2^167-1)-1)/167 = 2 3 499 1163 2657 155377 * 779261118849566979810923467921891 = 1120192871726680081018393165195713931233289613778

2349023-1 = 2349022
2349022/167 = 14066
14066 = 2*13*541

* Algo: Search: 000... string -> guess factors (prime components of group)
There are 1497 groups of p (167) + value 1. Factor = 250000 remainder: 927 Resulting in : 748288838313422294120286634350736906063837462 divisions of 187072209578355573530071658587684226515959365500927
There are 2000 groups of p (167) + value 1. Factor = 334001 remainder: 7289 Resulting in : 560094758932924073670652658488101013218401638 divisions of 187072209578355573530071658587684226515959365500927
"factors: 253341 -> 2*13*54[1]?"
"go to group 14066 after getting to group 2000"
* Algo: Search 3413 "1 sigma"
There are 1402 groups of p (167) + value 1. Factor = 234135 remainder: 8707 Resulting in : 798992929627589098298296532289850840395324772 divisions of 187072209578355573530071658587684226515959365500927
"binary base 10 group # -> factor: 234135 -> 2*13*54[1]?




#########
M#: 2^167-1 = 187072209578355573530071658587684226515959365500927 = 2349023 * ...
Factor: 8017, division: 23334440511208129416249427290468283212668001185, remainder: 782, significance: 10, Number: 187072209578355573530071658587684226515959365500927
Factor: 15031, division: 12445759402458623746262501402946192968928172809, remainder: 8848, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 16033, division: 11667947955987998099549158522278065646850830505, remainder: 14262, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 21377, division: 8751097421450885228519982157818413552694922837, remainder: 14378, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 24049, division: 7778793695303570773423911954246921972471178240, remainder: 7167, significance: 3, Number: 187072209578355573530071658587684226515959365500927
Factor: 28057, division: 6667577060211554105216939037947187030543513757, remainder: 20778, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 31063, division: 6022348439569763819659133328644503960208587885, remainder: 29172, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 36073, division: 5185934343646371899483593230052510922738872993, remainder: 24438, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 37409, division: 5000727353801373293327051206599594389477381525, remainder: 32202, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 39079, division: 4787026525201657502240887908792042440081869175, remainder: 11102, significance: 3, Number: 187072209578355573530071658587684226515959365500927
Factor: 44089, division: 4243058576478386298851678617970111059809915523, remainder: 7380, significance: 5, Number: 187072209578355573530071658587684226515959365500927
Factor: 49433, division: 3784358820592631916534939384372468321080237199, remainder: 42760, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 53441, division: 3500537220081128226082439673428345774142687552, remainder: 34495, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 56113, division: 3333847942158779133713607516755194456114614536, remainder: 42359, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 59119, division: 3164333117582428213096832804812060869026190657, remainder: 49744, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 63127, division: 2963426261003304030447695258568983580971048291, remainder: 34970, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 69473, division: 2692732566297058908209975941555485246296537726, remainder: 62529, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 71143, division: 2629523770129957599905425109816626042139906463, remainder: 3718, significance: 19, Number: 187072209578355573530071658587684226515959365500927
Factor: 76487, division: 2445803987322755154863854754241691091505214814, remainder: 22509, significance: 3, Number: 187072209578355573530071658587684226515959365500927
Factor: 77489, division: 2414177619769974751643093324054823607427626701, remainder: 67138, significance: 1, Number: 187072209578355573530071658587684226515959365500927
Factor: 79159, division: 2363246245889356529643775926776288564988938282, remainder: 36089, significance: 2, Number: 187072209578355573530071658587684226515959365500927
Factor: 84503, division: 2213793706476167396779660587052344017561025827, remainder: 41946, significance: 2, Number: 187072209578355573530071658587684226515959365500927
Factor: 88177, division: 2121553348133363275344723211128573511414080378, remainder: 10021, significance: 8, Number: 187072209578355573530071658587684226515959365500927

2349023-1 =2349022
2349022/167 = 14066
14066 = 2*13*541
88177/e = 32438.5054841742695913284994815271349087692881009881923433975256632770623102980299606231752[100001564266463]

Factor: 53441, division: 35[00]53722[00]81128226082439673428345774142687552, remainder: (34495), significance: 1, Number: 187072209578355573530071658587684226515959365500927
53441/(e) = 19659.84521(564304910)83863858011986302171715926534687048439333(06519514039)79410319039120930748832029168146351089011981994792392663334249728(43500169944)761
                        1406:34495                                          34495:1406                                                                    34495:1406

Factor: 63127, division: 29634262[6100330403044]7695258568983580971048291, remainder: 34970, significance: 1, Number: 187072209578355573530071658587684226515959365500927
23223.12548282963943536062903898254017925171926864240808897621378075565184188828988652663598763486935079334316275133088551[1006]8408714624739275747611439939738802609538294292231886754335927570673230[1004]

Factor: 143287, division: 13055769858979221669102686118607[00]7370938[00]313, remainder: 52096, significance: 2, Number: 187072209578355573530071658587684226515959365500927
143287*(e) = 389494.44835441121463906951090770895131614266431498610761829189047369976813346877418334963821433552188679937690076092244701745174202018576643017[0200165004]96614157476697211213896099148578212348308793276

Factor: 189713, division: 98608[00]7663341770743212989403828[00]67870727707, remainder: 22836, significance: 8, Number: 187072209578355573530071658587684226515959365500927
189713*(e) = 515693.40052
189713/e = 69791.51242295783715685[1601008641]

Factor: 2[00]401, division: 933489[40164]1486686843237601547318758469066349, remainder: 94978, significance: 2, Number: 187072209578355573530071658587684226515959365500927
73723.407890198212[690064]559064126919297007996469897305803205002896972981208773665492575670926196642526467862612804813483727902195850934643457790005

Factor: 2272537, division: 823186639330209248650612327049831208539[0000]9, remainder: 748094, significance: 3, Number: 187072209578355573530071658587684226515959365500927
2272537/e = 836019.[64160]142601


#########




2^193-1 = 12554203470773361527671578846415332832204710888928069025791
13821503
(13821503-1)/193
71614 = 2*61*587 = 10001011110111110
* Algo: Search 00... most 0s
There are 1043 groups of p (193) + value 1. Factor = 201300 remainder: 8191 Resulting in : 62365640689385799938755980359738364789889274162583552 divisions of 12554203470773361527671578846415332832204710888928069025791
There are 10500 groups of p (193) + value 1. Factor = 2026501 remainder: 6145 Resulting in : 6195014693194506949501420846283980532062264409900646 divisions of 12554203470773361527671578846415332832204710888928069025791
"Most 0s: 3 0s in group & factor"
"2026501 -> 2*61*5"
"2^13-1 = 8191 -> 13 lineage #; 1043 : 34.1 : 1 sigma 1 side : 107th prime 587
There are 56 groups of p (193) + value 1. Factor = 10809 remainder: 2800 Resulting in : 1161458365322727498165563775225768603219975103055608199 divisions of 12554203470773361527671578846415332832204710888928069025791
There are 6400 groups of p (193) + value 1. Factor = 1235201 remainder: 5562 Resulting in : 10163692768038045247430643957068795145247381510319429 divisions of 12554203470773361527671578846415332832204710888928069025791
*lattice like #: Iron 26 to 56 amu
* Algo: search 3413 : first and last group # values
There are 74 groups of p (193) + value 1. Factor = 14283 remainder: 4870 Resulting in : 8789612455907975584731204121273774999793258[3413]3450187 divisions of 12554203470773361527671578846415332832204710888928069025791


M137 = 174224571863520493293247799005065324265471 = 32032215596496435569 * ...
(32032215596496435569-1)/137 = 233811792675156464 = 2*2*2*2*8779*59497*27977333

* Algo: Search 000...
There are 219 groups of p (137) + value 1. Factor = 30004 remainder: 7207 Resulting in : 5806711500583938584630309258934319566 divisions of 174224571863520493293247799005065324265471
###############
~ Anthropologically significant (A. Greek) camel = 219, 34 = 1 sigma 1 side of standard normal distribution, the truth = 72... @ M137 (alpha : fine structure of spectral lines)
~ 000... significance
###############
There are 100 groups of p (137) + value 1. Factor = 13701 remainder: 7912 Resulting in : 12716193844501897182194569666817409259 divisions of 174224571863520493293247799005065324265471
37792 -> 27977333
There are 27 groups of p (137) + value 1. Factor = 3700 remainder: 171 Resulting in : 47087722125275808998175080812179817369 divisions of 174224571863520493293247799005065324265471
3772 -> 27977333
There are 127 groups of p (137) + value 1. Factor = 17400 remainder: 271 Resulting in : 10012906428937959384669413735923294498 divisions of 174224571863520493293247799005065324265471
~ centered-hexagon sequence: 1,7,19,37,61,91,127, _, _, 271 (open 8 position from 0 or 1 start); 27977333 = 8 length
There are 8098 groups of p (137) + value 1. Factor = 1109427 remainder: 9565 Resulting in : 157040140418[00000]6573887059720[9778]78 divisions of 174224571863520493293247799005065324265471
8098 & 1109427 & 9565 -> 8779*59497
There are 31 groups of p (137) + value 1. Factor = 4248 remainder: 1471 Resulting in : 41013317293672432507826694681041743[000] divisions of 174224571863520493293247799005065324265471
4248 -> 2*2*2*2


M149 = 713623846352979940529142984724747568191373311 = 86656268566282183151 * ...
(86656268566282183151-1)/149 = 581585695075719350 = 2*5*5*307*37888318897441
* Algo: Search 000...
There are 1500 groups of p (149) + value 1. Factor = 223501 remainder: 5870 Resulting in : 3192933572346342703295032168646885553941 divisions of 713623846352979940529142984724747568191373311
There are 151 groups of p (149) + value 1. Factor = 22500 remainder: 8311 Resulting in : 31716615393465775134628577098877669697394 divisions of 713623846352979940529142984724747568191373311
1500 & 223501 -> 2*5*5*30_; remainder 8311 -> 37888318897441
There are 1200 groups of p (149) + value 1. Factor = 178801 remainder: 2110 Resulting in : 3991162501065318094021526639810446072401 divisions of 713623846352979940529142984724747568191373311
There are 1351 groups of p (149) + value 1. Factor = 201300 remainder: 3811 Resulting in : 3545076236229408547089632313585432529515 divisions of 713623846352979940529142984724747568191373311
178801 & 3811-> 37888318897441


M157 = 182687704666362864775460604089535377456991567871 = 852133201 * ...
(852133201-1)/157 = 5427600 = 2*2*2*2*3*5*5*4523
* Algo: Search 000 and 3413
There are 523 groups of p (157) + value 1. Factor = 82112 remainder: 4927 Resulting in : 222486[000]421817596423739044[3413]086728577937 divisions of 182687704666362864775460604089535377456991567871
"523 groups -> 2*2*2*2*3*5*5*_523 ;"


M199 = 164,504,919,713
M257 = 535,006,138,814,359




M227 = 2^227-1 = 215679573337205118357336120696157045389097155380324579848828881993727 = 26986333437777017 *
(26986333437777017-1)/227 = 118882526157608 = 2*2*2*89*137*1218757957
Factor: 45[000]49, division: 4792827218930396499178922733867054456275857338[00]50879412386150, remainder: 72377, significance: 62, Number: 215679573337205118357336120696157045389097155380324579848828881993727
~ 4500049*e = 12232401.4238752980523378262751730775[2037000202]6757409385370527706172103316345851499403353[801089]996656578542709547218437798114530238096930901186576445053148107507501647309848477665599632921053535536153
Factor: 11855303, division: 1819266646640[791200000001018077]3704846607223398703903210978992, remainder: 5199151, significance: 2, Number: 215679573337205118357336120696157045389097155380324579848828881993727
~ rare 0000000; 791211877 : 1218757957 (missing 5);
Factor: 18425137, division: 117057242688184689404[120100000427158500]42100386028314462401494, remainder: 6039049, significance: 3, Number: 215679573337205118357336120696157045389097155380324579848828881993727
~ rare 00000; 1214271585 : 1218757957
Factor: 20000063, division: 10783944697434458999320958173789604832199636340161757482905372, remainder: 18955291, significance: 1, Number: 215679573337205118357336120696157045389097155380324579848828881993727
~ 18955291 : 1218757957
20000063/e = 7357611.99983364023277673592122673752095087170673661169153[0310027]6677368549722279958305569871966766678701025430583723453366376354152775259506606152690581[1379006]72221557919070732740278867593532110735841
10783944697434458999320958173789604832199636340161757482905372/e = 396719154891592742569351527[22098]86587060379695248[22089]1744790.3229404291880167656759052027777476[8902]68564282624387429133880[130072]733643540322364264834595682640504907617784791627241412869043132582826691




Mersenne Prime examples:
2^89-1 = 618970019642690137449562111
* Algo: Search 341 string, unusual 2 occurrences in 1 entry
There are 431 groups of p (89) + value 1. Factor = 38360 remainder: 9271 Resulting in : 16135819073062829443419 divisions of 618970019642690137449562111
* Algo: Search 3141 string
There are 91 groups of p (89) + value 1. Factor = 8100 remainder: 2011 Resulting in : 76416051807739523141921 divisions of 618970019642690137449562111
"91 groups, 8100 factor -> "89" again"

2^107-1 = 162259276829213363391578010288127
((2^107-1)-1)/107 = 1516441839525358536369887946618                                                         ~ Groups of p +1
((2^107-1)-1)/107 = 2 &times; 3 &times; (6361 &times; 69431 &times; 20394401) &times; 28059810762433 = 1516441839525358536369887946618    ~ factorization of Groups of p (71) +1
M53 = (6361 &times; 69431 &times; 20394401)
53*2 = 106 -> 1 less tha 107
Algo: Search: 000... string -> guess factors
* Note: there are NO 0000 strings from 1 < p < 470000
There are 471443 groups of p (107) + value 1. Factor = 50444402 remainder: 3301 Resulting in : 3216596300005962274893813 divisions of 162259276829213363391578010288127
There are 1756093 groups of p (107) + value 1. Factor = 187901952 remainder: 2047 Resulting in : 863531619028701540000915 divisions of 162259276829213363391578010288127
* Algo: Search 3413 & 341 strings -> extremely common at low group number
There are 8 groups of p (107) + value 1. Factor = 857 remainder: 649 Resulting in : 189334045308300307341397911654 divisions of 162259276829213363391578010288127

 */

