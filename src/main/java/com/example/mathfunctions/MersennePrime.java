package com.example.mathfunctions;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Checks for low factors. Then use probabilistic isProbablePrime() method to check higher numbers. --> balancing factorization time : isProbablePrime() time
// an array size of 1 would still be faster than purely probabilistic prime detection
// time                     primes        numberOfTerms                                    checkFactors()
//10125ms/0.168 minutes (2+21 primes); 1000 iterations  //value.isProbablePrime(10);  arraySize = 90000;
//232981ms/3.88 minutes (2+24 primes); 2000 iterations  //value.isProbablePrime(10);  arraySize = 500000;
//3292485ms/54.87 minutes (2+25 primes); 4000 iterations //value.isProbablePrime(10);  arraySize = 1000000;

public class Mprime_GithubV {
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
                    if (number.compareTo(BigInteger.valueOf(18000)) > 0) {
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
