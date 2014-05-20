package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;

public class Project2 {

    public static final Random RANDOM = new SecureRandom();
//    public static final long PRIME = powerLong(2, 31) - 1;
    public static final long PRIME = powerLong(2, 28) - 57;
//    public static final long PRIME = powerLong(2, 20) - 3;
//    public static final long PRIME = powerLong(2, 15) - 19;

    /**
     * The total amount of data consumes just under 6 gb of memory so set compile flag -Xmx8192m
     */
    public static void main(String[] args) throws Exception {

//        Data data = Data.SET1;
//        Data data = Data.SET2;
//        Data data = Data.SET3;
//        Data data = Data.SET4;
//        Data data = Data.SET5;
//        Data data = Data.SET6;
        Data data = Data.SET7;

        System.out.println("start loading data");
        data.init();
        System.out.println("done loading data");

//        benchmarkDeterministic(data);
        benchmarkRandomizedAlgorithm(data);

        //TODO Run the randomized algorithm twice
        //TODO Spurious true results occur when roots are found in both polynomials.
    }

    private static void benchmarkRandomizedAlgorithm(Data data) throws Exception {

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
        }
    }

    private static void benchmarkDeterministic(Data data) {

        while (true) {
            long timeBefore = System.currentTimeMillis();
            boolean result = deterministicMultisetEquality(data.getLinesA(), data.getLinesB());
            long timeAfter = System.currentTimeMillis();
            long totalTime = timeAfter - timeBefore;

            System.out.println("result: " + result + ", running time: " + totalTime + " ms");
        }
    }

    public static List<String> loadFile(File f) {
        try {
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
        SET1("ralgodata/data1a.txt", "ralgodata/data1b.txt", false),
        SET2("ralgodata/data2a.txt", "ralgodata/data2b.txt", false),
        SET3("ralgodata/data3a.txt", "ralgodata/data3b.txt", false),
        SET4("ralgodata/data4a.txt", "ralgodata/data4b.txt", false),
        SET5("ralgodata/data5a.txt", "ralgodata/data5b.txt", false),
        SET6("ralgodata/data6a.txt", "ralgodata/data6b.txt", false),
        SET7("ralgodata/data7a.txt", "ralgodata/data7b.txt", true);

        private final String name1;
        private final String name2;
        private List<String> linesA;
        private List<String> linesB;
        private boolean expectedResult;

        private Data(String name1, String name2, boolean expectedResult){
            this.name1 = name1;
            this.name2 = name2;
            this.expectedResult = expectedResult;
        }

        public void init() {
            this.linesA = loadFile(new File(name1));
            this.linesB = loadFile(new File(name2));
        }

        public  List<String> getLinesA() {
            return linesA;
        }

        public  List<String> getLinesB() {
            return linesB;
        }

        public boolean getExpectedResult() {
            return expectedResult;
        }
    }
}
