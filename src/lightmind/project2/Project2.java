package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;

public class Project2 {

    public static final Random RANDOM = new SecureRandom();
//    public static final long PRIME = powerLong(2, 31) - 1;    // 2.147.483.647
//    public static final long PRIME = powerLong(2, 24) - 167 ; // 16.777.049
    public static final long PRIME = powerLong(2, 20) - 185 ; // 1.048.391
//    public static final long PRIME = powerLong(2, 17) - 99;   // 130.973
//    public static final long PRIME = powerLong(2, 14) - 111;  // 16.273
//    public static final long PRIME = powerLong(2, 10) - 3;    // 1.021
//    public static final long PRIME = 101
//    public static final long PRIME = 11;


    /**
     * The total amount of data consumes just under 6 gb of memory so set compile flag -Xmx8192m
     */
    public static void main(String[] args) throws Exception {

//        Data data = Data.SET1; // 10 lines
//        Data data = Data.SET2; // 100 lines
//        Data data = Data.SET3; // 1.000 lines
//        Data data = Data.SET4; // 10.000 lines
        Data data = Data.SET5; // 100.000 lines
//        Data data = Data.SET6; // 1.000.000 lines
//        Data data = Data.SET7; // 10.000.000 lines

        System.out.println("start loading data");
//        data.initDataFile();
        data.initDataRandom();
        System.out.println("done loading data");

//        benchmarkDeterministic(data);
        benchmarkRandomizedAlgorithm(data, 1000);

        //TODO Run the randomized algorithm twice
    }

    private static void benchmarkRandomizedAlgorithm(Data data, int maxIterations) throws Exception {

        int errorCounter = 0;
        int iterations = 0;

        while (true) {
            long timeBefore = System.currentTimeMillis();
            boolean result = randomizedMultisetEquality(data.getLinesA(), data.getLinesB());
            long timeAfter = System.currentTimeMillis();
            long totalTime = timeAfter - timeBefore;

            if (result != data.getExpectedResult()) {
                errorCounter++;
            }

            iterations++;

            System.out.println("result: " + result + ", running time: " + totalTime + " ms, errors: " + errorCounter + ", iterations: " + iterations);

            if (iterations == maxIterations) {
                break;
            }
        }
    }

    private static void benchmarkDeterministic(Data data, int maxIterations) {

        int iterations = 0;

        while (true) {
            long timeBefore = System.currentTimeMillis();
            boolean result = deterministicMultisetEquality(data.getLinesA(), data.getLinesB());
            long timeAfter = System.currentTimeMillis();
            long totalTime = timeAfter - timeBefore;

            iterations++;

            System.out.println("result: " + result + ", running time: " + totalTime + " ms");

            if (iterations == maxIterations) {
                break;
            }
        }
    }

    public static List<String> loadFile(File f, double size) {
        try {
            FileInputStream fInput = new FileInputStream(f);
            InputStreamReader inReader = new InputStreamReader(fInput, "Cp1252");
            List<String> elements = new ArrayList<String>((int) size);
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
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when loading data", e);
        }
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

    public static boolean randomizedMultisetEquality(List<String> linesA, List<String> linesB) {
        List<Long> as = generateHashFunction(80); // length of lines is at most 80
        long zValue = Math.abs(RANDOM.nextLong()) % PRIME;

        long poly1 = fingerprintFile(linesA, as, zValue);
        long poly2 = fingerprintFile(linesB, as, zValue);

        System.out.print("poly1: " + poly1 + ", poly2: " + poly2 + ", ");

        return poly1 == poly2;
    }

    public static long fingerprintFile(List<String> lines, List<Long> as, long zValue) {
        long result = 1;

        for (String line : lines) {
            long hashValue = hash(line, as);
            long value = zValue - hashValue;

            // we do not want negative values, javas % operator does not what we want with negative values.
            while (value < 0) {
                value = PRIME + value;
            }

            result = (result * value) % PRIME;
        }

        return result;
    }

    public static List<Long> generateHashFunction(int size) {
        List<Long> a = new ArrayList<Long>(size + 5);

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

    private enum Data {
        SET1("ralgodata/data1a.txt", "ralgodata/data1b.txt", false, Math.pow(10.0, 1.0)),
        SET2("ralgodata/data2a.txt", "ralgodata/data2b.txt", false, Math.pow(10.0, 2.0)),
        SET3("ralgodata/data3a.txt", "ralgodata/data3b.txt", false, Math.pow(10.0, 3.0)),
        SET4("ralgodata/data4a.txt", "ralgodata/data4b.txt", false, Math.pow(10.0, 4.0)),
        SET5("ralgodata/data5a.txt", "ralgodata/data5b.txt", false, Math.pow(10.0, 5.0)),
        SET6("ralgodata/data6a.txt", "ralgodata/data6b.txt", false, Math.pow(10.0, 6.0)),
        SET7("ralgodata/data7a.txt", "ralgodata/data7b.txt", true, Math.pow(10.0, 7.0));
        private final String name1;
        private final String name2;
        private List<String> linesA;
        private List<String> linesB;
        private boolean expectedResult;
        private double size;

        private Data(String name1, String name2, boolean expectedResult, double size) {
            this.name1 = name1;
            this.name2 = name2;
            this.expectedResult = expectedResult;
            this.size = size;
        }

        public void initDataFile() {
            this.linesA = loadFile(new File(name1), size);
            this.linesB = loadFile(new File(name2), size);
        }

        public void initDataRandom() {
            linesA = new ArrayList<String>();
            linesB = new ArrayList<String>();
            String characters = "ABCDEFGHIJKLMOPQRSTUVWZXabcdefghijklmnopqrstuvwzx ";
            int length = RANDOM.nextInt(80 - 20) + 20;

            for (int i = 0; i < size; i++) {
                String firstString = generateString(characters, length);
                linesA.add(firstString);

                if (expectedResult) {
                    linesB.add(firstString);
                } else {
                    linesB.add(generateString(characters, length));
                }
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
    }
}
