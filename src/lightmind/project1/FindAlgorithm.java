package lightmind.project1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lukas on 28-04-14.
 */
public class FindAlgorithm implements RandomizedAlgorithm {
    private Random r = new Random();

    @Override
    public void setRandomSource(Random source) {
        r = source;
    }

    @Override
    public Random getRandomSource() {
        return r;
    }

    public int algorithm(List<Integer> L, int k, Callback iteration, Callback comparison){
        int pivot = L.get(r.nextInt(L.size()));
        iteration.callback(0);
        ArrayList<Integer> l1 = new ArrayList<Integer>();
        ArrayList<Integer> l2 = new ArrayList<Integer>();
        for(int element : L){
            if( element < pivot){
                l1.add(element);
            } else if( element > pivot ){
                l2.add(element);
            }
            comparison.callback(1);
        }

        if(l1.size() == k-1){
            comparison.callback(1);
            return pivot;
        } else if(l1.size()  > k-1){
            comparison.callback(1);comparison.callback(1);
            return algorithm(l1,k,iteration,comparison);
        } else {
            comparison.callback(1);comparison.callback(1);
            return algorithm(l2,k-1-l1.size(),iteration,comparison);
        }
    }
}
