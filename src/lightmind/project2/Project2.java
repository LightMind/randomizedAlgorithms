package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;

public class Project2 {

    public static final DecimalFormat format = new DecimalFormat("#.###");
    public static final Random RANDOM = new SecureRandom();

    /**
     * The largest set consumes just under 3.5 gb of memory so set compile flag -Xmx4048m
     */
    public static void main(String[] args) throws Exception {

        long prime20 = powerLong(2, 20) - 185; // 1.048.391
        long prime21 = powerLong(2, 21) - 9; // 2.097.143

        Data data = Data.SET6;

        //for (Data data : Data.values()) {
            System.out.println("\n****** " + (int) data.getSize() + " lines with " + (int) data.getIterations() + " iterations ******");

            System.out.println("\nrandomized single pass");
            data.initData1Different();
           benchmarkRandomizedAlgorithmSinglePass(data, prime20);
         /*   benchmarkRandomizedAlgorithmSinglePass(data, prime21);
            data.initDataAllDifferent();
            benchmarkRandomizedAlgorithmSinglePass(data, prime20);
            benchmarkRandomizedAlgorithmSinglePass(data, prime21);

            System.out.println("\nrandomized double pass");
            data.initData1Different();
            benchmarkRandomizedAlgorithmDoublePass(data, prime20);
            benchmarkRandomizedAlgorithmDoublePass(data, prime21);
            data.initDataAllDifferent();
            benchmarkRandomizedAlgorithmDoublePass(data, prime20);
            benchmarkRandomizedAlgorithmDoublePass(data, prime21);

            System.out.println("\ndeterministic single pass");
            data.initData1Different();
            */
            benchmarkDeterministic(data);

            //System.out.println("");
        //}
    }

    private static void benchmarkRandomizedAlgorithmSinglePass(Data data, long prime) throws Exception {
        int errorCounter = 0;
        double totalTime = 0.0;

        for (int i = 0; i < data.getIterations(); i++) {
            double timeBefore = System.nanoTime();
            boolean result = randomizedMultisetEqualitySinglePass(data.getLinesA(), data.getLinesB(), prime);
            totalTime += System.nanoTime() - timeBefore;

            if (result != data.getExpectedResult()) {
                errorCounter++;
            }

            System.out.println(format.format((totalTime / (i + 1)) * Math.pow(10.0, -6.0)));
        }

        int bitsInPrime = (int) (Math.log(prime) / Math.log(2)) + 1;
        String averageTime = format.format((totalTime / data.getIterations()) * Math.pow(10.0, -6.0));
        System.out.println(bitsInPrime + " bit prime, " + data.getDataType() + ", " + errorCounter + " errors, " + averageTime + " ms");
    }

    private static void benchmarkRandomizedAlgorithmDoublePass(Data data, long prime) throws Exception {
        int errorCounter = 0;
        double totalTime = 0.0;

        for (int i = 0; i < data.getIterations(); i++) {
            double timeBefore = System.nanoTime();
            boolean result = randomizedMultisetEqualityDoublePass(data.getLinesA(), data.getLinesB(), prime);
            totalTime += System.nanoTime() - timeBefore;

            if (result != data.getExpectedResult()) {
                errorCounter++;
            }
        }

        int bitsInPrime = (int) (Math.log(prime) / Math.log(2)) + 1;
        String averageTime = format.format((totalTime / data.getIterations()) * Math.pow(10.0, -6.0));
        System.out.println(bitsInPrime + " bit prime, " + data.getDataType() + ", " + errorCounter + " errors, " + averageTime + " ms");
    }

    private static void benchmarkDeterministic(Data data) {
        double totalTime = 0.0;

        for (int i = 0; i < data.getIterations(); i++) {
            Collections.shuffle(data.getLinesA());
            Collections.shuffle(data.getLinesB());
            long timeBefore = System.nanoTime();
            deterministicMultisetEquality(data.getLinesA(), data.getLinesB());
            totalTime += System.nanoTime() - timeBefore;

            System.out.println(format.format((totalTime / (i + 1)) * Math.pow(10.0, -6.0)));
        }

        String averageTime = format.format((totalTime / data.getIterations()) * Math.pow(10.0, -6.0));
        System.out.println(averageTime + " ms");
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

    public static boolean randomizedMultisetEqualitySinglePass(List<String> linesA, List<String> linesB, long prime) {
        List<Long> as = generateHashFunction(80, prime);
        long zValue = Math.abs(RANDOM.nextLong()) % prime;

        long poly1 = fingerprintFileSinglePass(linesA, as, zValue, prime);
        long poly2 = fingerprintFileSinglePass(linesB, as, zValue, prime);

        return poly1 == poly2;
    }

    public static boolean randomizedMultisetEqualityDoublePass(List<String> linesA, List<String> linesB, long prime) {
        List<Long> as1 = generateHashFunction(80, prime);
        List<Long> as2 = generateHashFunction(80, prime);
        long zValue1 = Math.abs(RANDOM.nextLong()) % prime;
        long zValue2 = Math.abs(RANDOM.nextLong()) % prime;

        long polys1[] = fingerprintFileDoublePass(linesA, as1, as2, zValue1, zValue2, prime);
        long polys2[] = fingerprintFileDoublePass(linesB, as1, as2, zValue1, zValue2, prime);

        return polys1[0] == polys2[0] && polys1[1] == polys2[1];
    }

    public static long fingerprintFileSinglePass(List<String> lines, List<Long> as, long zValue, long prime) {
        long result = 1;

        for (String line : lines) {
            long hashValue = hash(line, as, prime);
            long value = zValue - hashValue;

            // we do not want negative values, javas % operator does not what we want with negative values.
            while (value < 0) {
                value = prime + value;
            }

            result = (result * value) % prime;
        }

        return result;
    }

    public static long[] fingerprintFileDoublePass(List<String> lines, List<Long> as1, List<Long> as2, long zValue1, long zValue2, long prime) {
        long result1 = 1;
        long result2 = 1;

        for (String line : lines) {
            long hashValue1 = hash(line, as1, prime);
            long hashValue2 = hash(line, as1, prime);
            long value1 = zValue1 - hashValue1;
            long value2 = zValue2 - hashValue2;

            // we do not want negative values, javas % operator does not what we want with negative values.
            while (value1 < 0) {
                value1 = prime + value1;
            }

            while (value2 < 0) {
                value2 = prime + value2;
            }

            result1 = (result1 * value1) % prime;
            result2 = (result2 * value2) % prime;
        }

        long[] results = {result1, result2};
        return results;
    }

    public static List<Long> generateHashFunction(int size, long prime) {
        List<Long> a = new ArrayList<Long>(size + 5);

        for (int i = 0; i < size; i++) {
            a.add(Math.abs(RANDOM.nextLong()) % prime);
        }

        return a;
    }

    public static long hash(String x, List<Long> as, long prime) {
        char[] chars = x.toCharArray();
        long result = 0;

        if (x.length() > 80) {
            System.out.println(x.length());
        }

        for (int i = 0; i < x.length(); i++) {
            Long aLong = as.get(i);
            char aChar = chars[i];
            result += (aChar * aLong);
            result %= prime;
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

    private enum Data {
        SET1(false, Math.pow(10.0, 1.0), Math.pow(10.0, 3.0)),
        SET2(false, Math.pow(10.0, 2.0), Math.pow(10.0, 3.0)),
        SET3(false, Math.pow(10.0, 3.0), Math.pow(10.0, 6.0)),
        SET4(false, Math.pow(10.0, 4.0), Math.pow(10.0, 3.0)),
        SET5(false, Math.pow(10.0, 5.0), Math.pow(10.0, 1.0)),
        SET6(false, Math.pow(10.0, 6.0), Math.pow(10.0, 1.0));

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
}
