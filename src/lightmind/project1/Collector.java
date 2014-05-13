package lightmind.project1;

/**
 * Created by Lukas on 28-04-14.
 */
public class Collector implements Callback {
    int counter = 0;

    @Override
    public void callback(int status) {
        counter = counter + 1;
    }

    @Override
    public int collect() {
        int res = counter;
        counter = 0;
        return res;
    }

    @Override
    public void reset(){
        counter = 0;
    }
}
