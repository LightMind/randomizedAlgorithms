package lightmind.project2;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Project2 {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.###");
    public static final Random RANDOM = new SecureRandom();
    public static final long PRIME = powerLong(2, 20) - 185; // 1.048.391
    //public static final long PRIME = powerLong(2, 21) - 9; // 2.097.143
    public static final int SKIP = 100;

    /**
     * The largest set consumes just under 500 mb of memory so set compile flag -Xmx1024m
     * Run the code twice with the different primes above.
     */
    public static void main(String[] args) throws Exception {

        int bitsInPrime = (int) (Math.log(PRIME) / Math.log(2)) + 1;
        System.out.println("****** benchmarking with a " + bitsInPrime + " bit prime and skipping the first " + SKIP + " iterations when calculating the average running time ******");

        RandomizedBenchmarkStrategy singlePolynomialRandomizedBenchmarkStrategy = new SinglePolynomialRandomizedBenchmarkStrategy();
        RandomizedBenchmarkStrategy doublePolynomialRandomizedBenchmarkStrategy = new DoublePolynomialRandomizedBenchmarkStrategy();

        for (Data data : Data.values()) {
            System.out.println("");
            System.out.println("****** " + (int) data.getSize() + " lines with " + (int) data.getIterations() + " iterations ******");

            System.out.println("randomized - all lines in the two sets are different");
            data.initDataAllDifferent();
            randomizedBenchmark(data, singlePolynomialRandomizedBenchmarkStrategy);
            randomizedBenchmark(data, doublePolynomialRandomizedBenchmarkStrategy);

            System.out.println("randomized - only one pair of lines in the two sets are different");
            data.initData1Different();
            randomizedBenchmark(data, singlePolynomialRandomizedBenchmarkStrategy);
            randomizedBenchmark(data, doublePolynomialRandomizedBenchmarkStrategy);

            System.out.println("deterministic - only one pair of lines in the two sets are different");
            deterministicBenchmark(data);
        }
    }

    private static void randomizedBenchmark(Data data, RandomizedBenchmarkStrategy randomizedBenchmarkStrategy) throws Exception {
        int errorCounter = 0;
        long totalTime = 0;

        for (int i = 0; i < data.getIterations(); i++) {
            long timeBefore = System.nanoTime();
            boolean result = randomizedBenchmarkStrategy.randomizedMultisetEquality(data.getLinesA(), data.getLinesB());

            if (i >= SKIP) {
                totalTime += System.nanoTime() - timeBefore;
            }

            if (result != data.getExpectedResult()) {
                errorCounter++;
            }
        }

        String averageTime = FORMAT.format((totalTime / (data.getIterations() - SKIP)) * Math.pow(10.0, -6.0));
        System.out.println("    " + errorCounter + " errors, " + averageTime + " ms, " + randomizedBenchmarkStrategy.getPolynomialType());
    }

    private static void deterministicBenchmark(Data data) {
        long totalTime = 0;

        for (int i = 0; i < data.getIterations(); i++) {
            Collections.shuffle(data.getLinesA());
            Collections.shuffle(data.getLinesB());

            long timeBefore = System.nanoTime();
            deterministicMultisetEquality(data.getLinesA(), data.getLinesB());

            if (i >= SKIP) {
                totalTime += System.nanoTime() - timeBefore;
            }
        }

        String averageTime = FORMAT.format((totalTime / (data.getIterations() - SKIP)) * Math.pow(10.0, -6.0));
        System.out.println("    " + averageTime + " ms");
    }

    public static boolean deterministicMultisetEquality(List<String> linesA, List<String> linesB) {
        Collections.sort(linesA);
        Collections.sort(linesB);

        for (int j = 0; j < linesA.size(); j++) {
            if (!linesA.get(j).equals(linesB.get(j))) {
                return false;
            }
        }

        return true;
    }

    public static List<Long> generateHashFunction(int size) {
        List<Long> a = new ArrayList<Long>(size);

        for (int i = 0; i < size; i++) {
            a.add(Math.abs(RANDOM.nextLong()) % PRIME);
        }

        return a;
    }

    public static long hash(String x, List<Long> as) {
        char[] chars = x.toCharArray();
        long result = 0;

        if (x.length() > 80) {
            System.out.println(x.length());
        }

        for (int i = 0; i < x.length(); i++) {
            Long aLong = as.get(i);
            char aChar = chars[i];
            result += (aChar * aLong);
            result %= PRIME;
        }

        return result;
    }

    public static Long powerLong(long base, long exponent) {
        long result = 1;

        for (int i = 0; i < exponent; i++) {
            result = result * base;
        }

        return result;
    }

    public static enum Data {
        SET2(false, Math.pow(10.0, 2.0), Math.pow(10.0, 7.0)),
        SET3(false, Math.pow(10.0, 3.0), Math.pow(10.0, 6.0)),
        SET4(false, Math.pow(10.0, 4.0), Math.pow(10.0, 5.0)),
        SET5(false, Math.pow(10.0, 5.0), Math.pow(10.0, 4.0)),
        SET6(false, Math.pow(10.0, 6.0), Math.pow(10.0, 3.0));
        private List<String> linesA;
        private List<String> linesB;
        private boolean expectedResult;
        private double size;
        private double iterations;
        private String dataType;

        private Data(boolean expectedResult, double size, double iterations) {
            this.expectedResult = expectedResult;
            this.size = size;
            this.iterations = iterations;
        }

        public void initData1Different() {
            dataType = "one different";
            linesA = new ArrayList<String>();
            linesB = new ArrayList<String>();
            String characters = "ABCDEFGHIJKLMOPQRSTUVWZXabcdefghijklmnopqrstuvwzx ";
            int length = RANDOM.nextInt(39 - 30) + 30;

            for (int i = 0; i < size; i++) {

                if (i == 0) {
                    linesA.add(generateString(characters, length));
                    linesB.add(generateString(characters, length));
                } else {
                    String firstString = generateString(characters, length);
                    linesA.add(firstString);
                    linesB.add(firstString);
                }
            }
        }

        public void initDataAllDifferent() {
            dataType = "all different";
            linesA = new ArrayList<String>();
            linesB = new ArrayList<String>();
            String characters = "ABCDEFGHIJKLMOPQRSTUVWZXabcdefghijklmnopqrstuvwzx ";
            int length = RANDOM.nextInt(39 - 30) + 30;

            for (int i = 0; i < size; i++) {
                linesA.add(generateString(characters, length));
                linesB.add(generateString(characters, length));

            }
        }

        public String generateString(String characters, int length) {
            char[] text = new char[length];

            for (int i = 0; i < length; i++) {
                text[i] = characters.charAt(RANDOM.nextInt(characters.length()));
            }

            return new String(text);
        }

        public List<String> getLinesA() {
            return linesA;
        }

        public List<String> getLinesB() {
            return linesB;
        }

        public boolean getExpectedResult() {
            return expectedResult;
        }

        private double getIterations() {
            return iterations;
        }

        private double getSize() {
            return size;
        }

        private String getDataType() {
            return dataType;
        }
    }

    public static interface RandomizedBenchmarkStrategy {

        public boolean randomizedMultisetEquality(List<String> linesA, List<String> linesB);

        String getPolynomialType();
    }

    public static class SinglePolynomialRandomizedBenchmarkStrategy implements RandomizedBenchmarkStrategy {

        @Override
        public boolean randomizedMultisetEquality(List<String> linesA, List<String> linesB) {
            List<Long> as = generateHashFunction(80);
            long zValue = Math.abs(RANDOM.nextLong()) % PRIME;

            long polynomial1 = fingerprint(linesA, as, zValue);
            long polynomial2 = fingerprint(linesB, as, zValue);

            return polynomial1 == polynomial2;
        }

        @Override
        public String getPolynomialType() {
            return "single polynomial";
        }

        public long fingerprint(List<String> lines, List<Long> as, long zValue) {
            long result = 1;

            for (String line : lines) {
                long hashValue = hash(line, as);
                long value = zValue - hashValue;

                while (value < 0) {
                    value = PRIME + value;
                }

                result = (result * value) % PRIME;
            }

            return result;
        }
    }

    public static class DoublePolynomialRandomizedBenchmarkStrategy implements RandomizedBenchmarkStrategy {

        @Override
        public boolean randomizedMultisetEquality(List<String> linesA, List<String> linesB) {
            List<Long> as1 = generateHashFunction(80);
            List<Long> as2 = generateHashFunction(80);
            long zValue1 = Math.abs(RANDOM.nextLong()) % PRIME;
            long zValue2 = Math.abs(RANDOM.nextLong()) % PRIME;

            long polynomials1[] = fingerprint(linesA, as1, as2, zValue1, zValue2);
            long polynomials2[] = fingerprint(linesB, as1, as2, zValue1, zValue2);

            return polynomials1[0] == polynomials2[0] && polynomials1[1] == polynomials2[1];
        }

        @Override
        public String getPolynomialType() {
            return "double polynomial";
        }

        public long[] fingerprint(List<String> lines, List<Long> as1, List<Long> as2, long zValue1, long zValue2) {
            long result1 = 1;
            long result2 = 1;

            for (String line : lines) {
                long hashValue1 = hash(line, as1);
                long hashValue2 = hash(line, as2);
                long value1 = zValue1 - hashValue1;
                long value2 = zValue2 - hashValue2;

                while (value1 < 0) {
                    value1 = PRIME + value1;
                }

                while (value2 < 0) {
                    value2 = PRIME + value2;
                }

                result1 = (result1 * value1) % PRIME;
                result2 = (result2 * value2) % PRIME;
            }

            long[] results = {result1, result2};
            return results;
        }
    }
}
