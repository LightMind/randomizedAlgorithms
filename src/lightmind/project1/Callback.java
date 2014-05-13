package lightmind.project1;

/**
 * Created by Lukas on 28-04-14.
 */
public interface Callback {
    public void callback(int status);
    public int collect();
    public void reset();
}
