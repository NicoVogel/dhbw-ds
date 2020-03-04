package dhbw;

/**
 * BeforeSend
 */
@FunctionalInterface
public interface BeforeSend {

    void before(String message);

}
