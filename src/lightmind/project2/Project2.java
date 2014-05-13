package lightmind.project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Project2 {

    public static Random r = new SecureRandom();
    public static long prime =  powerLong(2,43)-57; // enough space to multiply with 16 bit numbers withouth overflow.

    public static void main(String[] args) throws Exception{

        File t  = new File("ralgodata");
        if(t.isDirectory()){
            File[] files = t.listFiles();
            for(File f : files){
                System.out.println(f.getCanonicalPath()
                      + " readable: "  +  f.canRead()
                      + " isFile " + f.isFile());
                List<String> content = loadFile(f);
                System.out.println(content.size());
            }
        }
    }

    public static Long powerLong(long base, long exponent){
        long result = base;
        for(int i = 1; i < exponent ; i++){
            result = result * base;
        }
        return result;
    }

    public static List<String> loadFile(File f) throws Exception{
        FileInputStream finput = new FileInputStream(f);
        InputStreamReader inreader = new InputStreamReader(finput,"Cp1252");
        List<String> elements = new ArrayList<String>(10000005);
        Scanner sc = new Scanner(inreader);
        String tempString = "";
        int tempLength = 0;

        while(sc.hasNextLine()){
            tempString = sc.nextLine();
            tempLength = tempString.length();
            tempString = tempString.substring(1,tempLength-1);
            elements.add(tempString);
        }
        sc.close();
        return elements;
    }

    public static List<Long> generateHashFunction(int size){
        List<Long> a = new ArrayList<Long>(size+5);

        for(int i = 0; i < size; i++){
            a.add(r.nextLong() % prime);
        }

        return a;
    }

    public Long hash(String x, List<Long> as){
        char[] chars = x.toCharArray();
        long result = 0;

        for(int i = 0; i < x.length() ; i++){
            result += (chars[i]*as.get(i));
            result %= prime;
        }

        return result;
    }

    public List<Long> hash(List<String> xs, List<Long> as){
        List<Long> hs = new ArrayList<Long>(xs.size());
        // hashing/fingerprinting of all the strings here

        return hs;
    }

    public static boolean randomizedEqual(List<String> setX, List<String> setY){
        List<Long> as = generateHashFunction(80);
        // do polynomialisation here

        return false;
    }

}
