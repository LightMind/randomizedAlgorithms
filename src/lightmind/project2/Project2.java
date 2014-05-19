package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.*;

public class Project2 {

    public static final Random r = new SecureRandom();
    public static final long prime = powerLong(2, 31) - 1;

    public static void main(String[] args) throws Exception {
        File output = new File("output"+System.currentTimeMillis()+".txt");
        PrintStream p = new PrintStream(output);
        System.setOut(p);


        //boolean[] results = runDeterministicBenchmark(2);
        benchmarkRandomizedAlgorithm();
    }

    private static void benchmarkRandomizedAlgorithm() throws Exception {
        System.out.println("Randomized Benchmark:");

        long[] trueCounter = new long[7];
        long[] falseCounter = new long[7];
        long[] timeCounter = new long[7];

        while(true){
            for(int i = 1; i <= 7; i++){
                for(int repeat = 0; repeat < powerLong(2,8-i-1); repeat++){
                    long time = System.currentTimeMillis();
                    boolean result = randomizedMultisetEquality(i);
                    long resultTime = System.currentTimeMillis()-time;

                    if(result){
                        trueCounter[i-1]++;
                    }else{
                        falseCounter[i-1]++;
                    }
                    timeCounter[i-1] += resultTime;
                }
                System.out.println(i + ", " + trueCounter[i-1] + ", " + falseCounter[i-1] + ", " + (trueCounter[i-1] + falseCounter[i-1]) + ", " + timeCounter[i-1]/(trueCounter[i-1] + falseCounter[i-1]));
            }
            System.out.println("");
        }
    }


    private static boolean[] runDeterministicBenchmark(int repeats) throws Exception {
        long[] timings = new long[7];
        boolean[] results = new boolean[7];
        for(int repeat = 0; repeat < repeats; repeat++){
            for(int i = 1; i <= 7; i++){
                long time = System.currentTimeMillis();
                results[i-1] = deterministicMultisetEquality(i);
                timings[i-1] += System.currentTimeMillis()-time;
            }
        }

        System.out.println("Deterministic benchmark");
        for(int i = 0; i < 7; i++){
            timings[i] /= repeats;
            System.out.println("time for data set " + (i+1) + " is " + timings[i] + " ms");
        }
        for(int i = 0; i < 7; i++){
            System.out.println("result for data set " + (i+1) + " is " + results[i]);
        }
        return results;
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

    public static boolean randomizedMultisetEquality(int i) throws Exception{
        return randomizedMultisetEqualitySingle(i) && randomizedMultisetEqualitySingle(i);
    }

    public static boolean randomizedMultisetEqualitySingle(int i) throws Exception {
        String setA = "ralgodata/data" + i + "a.txt";
        String setB = "ralgodata/data" + i + "b.txt";

        File fileA = new File(setA);
        File fileB = new File(setB);

        List<Long> as = generateHashFunction(80); // length of lines is at most 80
        long zValue = Math.abs(r.nextLong()) % prime;

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
                value = prime + value;
            }

            if(value >= prime){
                value = value % prime;
            }

            result = result * value;
            result = result % prime;
        }
        sc.close();
        return result;
    }

    public static List<Long> generateHashFunction(int size) {
        List<Long> a = new ArrayList<>(size + 5);

        for (int i = 0; i < size; i++) {
            a.add(Math.abs(r.nextLong()) % prime);
        }

        return a;
    }

    public static long hash(String x, List<Long> as) {
        char[] chars = x.toCharArray();
        long result = 0;

        for (int i = 0; i < x.length(); i++) {
            result += (chars[i] * as.get(i));
            result %= prime;
        }

        return result;
    }

}
