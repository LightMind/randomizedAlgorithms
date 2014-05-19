package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.*;

public class Project2 {

    public static final Random r = new SecureRandom();
    public static final long PRIME = powerLong(2, 31) - 1;
    private static PrintStream printStream;

    public static void main(String[] args) throws Exception {
        File output = new File("output" + System.currentTimeMillis() + ".txt");
        printStream = new PrintStream(output);
        System.setOut(printStream);

        //benchmarkDeterministic(1);
        benchmarkRandomizedAlgorithm(Arrays.asList(7));
    }

    private static void benchmarkRandomizedAlgorithm(List<Integer> dataSetsToRun) throws Exception {

        System.out.println("RANDOMIZED BENCHMARK:");
        System.out.println("dataset, true, false, iterations, average running time");

        long[] trueCounters = new long[7];
        long[] falseCounters = new long[7];
        long[] timings = new long[7];

        while (true) {
            for (int i = 1; i <= 7; i++) {
                if (dataSetsToRun.contains(i)) {
                    for (int iteration = 0; iteration < powerLong(2, 8 - i - 1); iteration++) {
                        long timeBefore = System.currentTimeMillis();
                        boolean result = randomizedMultisetEquality(i);
                        timings[i - 1] += System.currentTimeMillis() - timeBefore;

                        if (result) {
                            trueCounters[i - 1]++;
                        } else {
                            falseCounters[i - 1]++;
                        }
                    }

                    System.out.println(i +
                            ", " + trueCounters[i - 1] +
                            ", " + falseCounters[i - 1] +
                            ", " + (trueCounters[i - 1] + falseCounters[i - 1]) +
                            ", " + timings[i - 1] / (trueCounters[i - 1] + falseCounters[i - 1]));
                }
            }

            System.out.println("");
        }
    }

    private static void benchmarkDeterministic(int iterations) throws Exception {
        System.out.println("DETERMINISTIC BENCHMARK:");
        System.out.println("dataset, result, average running time");

        long[] timings = new long[7];
        boolean[] results = new boolean[7];

        for (int iteration = 1; iteration <= iterations; iteration++) {
            for (int i = 1; i <= 7; i++) {
                long timeBefore = System.currentTimeMillis();
                results[i - 1] = deterministicMultisetEquality(i);
                timings[i - 1] += System.currentTimeMillis() - timeBefore;
            }
        }

        for (int i = 1; i <= 7; i++) {
            long timing = timings[i - 1] / iterations;
            boolean result = results[i - 1];
            System.out.println(i +
                    ", " + result +
                    ", " + timing);
        }

        System.out.println("");
    }

    public static Long powerLong(long base, long exponent) {
        long result = 1;
        for (int i = 0; i < exponent; i++) {
            result = result * base;
        }
        return result;
    }

    public static List<String> loadFile(File f) throws Exception {
        FileInputStream fInput = new FileInputStream(f);
        InputStreamReader inReader = new InputStreamReader(fInput, "Cp1252");
        List<String> elements = new ArrayList<String>(10000005);
        Scanner sc = new Scanner(inReader);
        String tempString;
        int tempLength;

        while (sc.hasNextLine()) {
            tempString = sc.nextLine();
            tempLength = tempString.length();
            tempString = tempString.substring(1, tempLength - 1);
            elements.add(tempString);
        }
        sc.close();
        return elements;
    }

    public static boolean deterministicMultisetEquality(int i) throws Exception {
        String setA = "ralgodata/data" + i + "a.txt";
        String setB = "ralgodata/data" + i + "b.txt";

        File fileA = new File(setA);
        File fileB = new File(setB);

        List<String> as = loadFile(fileA);
        List<String> bs = loadFile(fileB);

        Collections.sort(as);
        Collections.sort(bs);

        for (int j = 0; j < as.size(); j++) {
            if (!as.get(j).equals(bs.get(j))) {
                return false;
            }
        }

        return true;
    }

    public static boolean randomizedMultisetEquality(int i) throws Exception {
        return randomizedMultisetEqualitySingle(i) && randomizedMultisetEqualitySingle(i);
    }

    public static boolean randomizedMultisetEqualitySingle(int i) throws Exception {
        String setA = "ralgodata/data" + i + "a.txt";
        String setB = "ralgodata/data" + i + "b.txt";

        File fileA = new File(setA);
        File fileB = new File(setB);

        List<Long> as = generateHashFunction(80); // length of lines is at most 80
        long zValue = Math.abs(r.nextLong()) % PRIME;

        long poly1 = fingerprintFile(fileA, as, zValue);
        long poly2 = fingerprintFile(fileB, as, zValue);

        return poly1 == poly2;
    }

    public static long fingerprintFile(File f, List<Long> as, long zValue) throws Exception {
        FileInputStream fInput = new FileInputStream(f);
        InputStreamReader inReader = new InputStreamReader(fInput, "Cp1252");
        Scanner sc = new Scanner(inReader);
        String tempString;
        int tempLength;

        long result = 1;

        while (sc.hasNextLine()) {
            tempString = sc.nextLine();
            tempLength = tempString.length();
            tempString = tempString.substring(1, tempLength - 1); // remove [ and ]
            long hashValue = hash(tempString, as);

            long value = zValue - hashValue;

            // we do not want negative values, javas % operator does not what we want with negative values.
            while (value < 0) {
                value = PRIME + value;
            }

            if (value >= PRIME) {
                value = value % PRIME;
            }

            result = result * value;
            result = result % PRIME;
        }
        sc.close();
        return result;
    }

    public static List<Long> generateHashFunction(int size) {
        List<Long> a = new ArrayList<Long>(size + 5);

        for (int i = 0; i < size; i++) {
            a.add(Math.abs(r.nextLong()) % PRIME);
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
}
