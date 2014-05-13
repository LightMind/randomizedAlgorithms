package lightmind.project1;

import java.util.List;
import java.util.Random;

/**
 * Created by Lukas on 28-04-14.
 */
public interface RandomizedAlgorithm {
    public void setRandomSource(Random source);
    public Random getRandomSource();
    public int algorithm(List<Integer> L, int k, Callback iteration, Callback comparison);
}
