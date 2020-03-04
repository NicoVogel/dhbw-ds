package dhbw;

/**
 * InnerProcessEnded
 */
@FunctionalInterface
public interface PostJob {

    void postRun(WorkerRunnable runner);

}
