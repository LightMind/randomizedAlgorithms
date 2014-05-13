package lightmind.project1;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Lukas on 28-04-14.
 */
public class Main {

    public static void main(String[] args){
        Random sr;
        sr = new SecureRandom();

        int[] ns = {10,100,1000,10000};
        float[] ks = {0.25f,0.5f,0.75f};

        testFindAlgorithm(ns, ks, sr, 10, 2f);
        System.out.println("----------");
        testQuicksortAlgorithm(ns, sr, 10 , 1f);
    }

    public static List<Integer> numbers(int size){
        List<Integer> result = new ArrayList<Integer>(size);
        for(int i = 0; i < size; i++){
            result.add(i);
        }
        return result;
    }

    public static void testFindAlgorithm(int[] ns, float[] ks, Random source, int repeatFactor, float myFactor){
        Callback cb = new Collector();
        Callback comp = new Collector();

        RandomizedAlgorithm rand = new FindAlgorithm();
        rand.setRandomSource(source);

        for(int n : ns){
            List<Integer> data = numbers(n);
            double my = 2 * Math.log(n);
            double my2 = myFactor*2*Math.log(n);
            System.out.println("my="+ my +" 2*my=" + my2);
            int repeats = n*repeatFactor;
            for( float kp : ks){
                int k = Math.min(n,Math.max((int)(n*kp),1));
                float above = 0;
                float totalZ= 0f;

                for(int repeat = 0; repeat < repeats; repeat++){
                    rand.algorithm(data,k,cb,comp);
                    int Z = cb.collect()-1;
                    totalZ += Z;
                    if(Z > my2){
                        above+=1;
                    }
                }

                System.out.println("n=" + n + " k=" + k + " above=" + above+ " repeats=" + repeats + " factor=" + above/repeats + " avg=" + totalZ/repeats);
            }
        }
    }

    public static void testQuicksortAlgorithm(int[] ns, Random source, int repeatFactor, float myFactor){
        Callback comp = new Collector();

        QuickSortAlgorithm rand = new QuickSortAlgorithm();
        rand.setRandomSource(source);

        for(int n : ns){
            List<Integer> data = numbers(n);
            double my = 2 * Math.log(n) * n;
            double my3 = myFactor * my;
            System.out.println("my="+ my +" "+ myFactor+ "*my=" + my3);
            int repeats = n*repeatFactor;

            float above = 0;
            float totalComparisons= 0f;

            for(int repeat = 0; repeat < repeats; repeat++){
                Collections.shuffle(data);
                rand.quicksort(data,0,data.size()-1, comp);
                int comparisons = comp.collect();
                totalComparisons += comparisons;
                if(comparisons > my3){
                    above+=1;
                }

                //test sorting
               /* for(int i = 1; i < data.size(); i++){
                    if(data.get(i-1) > data.get(i)){
                        System.out.println("Error");
                        break;
                    }
                }*/
            }

            System.out.println("n=" + n +" above="+ above+ " repeats=" + repeats + " factor=" + above/repeats + " avg=" + totalComparisons/repeats);
            System.out.println("");

        }
    }
}
