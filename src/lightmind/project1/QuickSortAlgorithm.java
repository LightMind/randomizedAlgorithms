package lightmind.project1;

import java.util.List;
import java.util.Random;

public class QuickSortAlgorithm implements RandomizedAlgorithm {

    private Random r = new Random();

    @Override
    public void setRandomSource(Random source) {
        r= source;
    }

    @Override
    public Random getRandomSource() {
        return r;
    }

    private int partition(List<Integer> L,int left, int right, int pivotIndex, Callback comparison){
        int pivotValue = L.get(pivotIndex);

        int temp = L.get(right);
        L.set(right,pivotValue);
        L.set(pivotIndex,temp);
        int storeIndex = left;
        for(int i = left; i < right; i++){
            comparison.callback(0);
            if(L.get(i) <= pivotValue){
                temp = L.get(i);
                L.set(i,L.get(storeIndex));
                L.set(storeIndex,temp);
                storeIndex = storeIndex+1;
            }
        }
        temp = L.get(storeIndex);
        L.set(storeIndex,L.get(right));
        L.set(right,temp);

        return storeIndex;
    }

    public void quicksort(List<Integer> L,int left, int right, Callback comparison) {
        if(left < right){
            int pivotIndex = left + r.nextInt(right-left + 1);
            int pivotNewIndex = partition(L,left,right,pivotIndex,comparison);
            quicksort(L,left,Math.max(pivotNewIndex-1,0),comparison);
            quicksort(L,Math.min(pivotNewIndex+1,L.size()-1),right,comparison);
        }
    }

    @Override
    public int algorithm(List<Integer> L, int k, Callback iteration, Callback comparison) {
        return 0;
    }


}
