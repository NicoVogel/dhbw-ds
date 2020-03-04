package dhbw;

import java.io.Closeable;

/**
 * interface to controle the server form a runner
 */
public interface ServerOperations {
    /**
     * close everthing which is open
     */
    void closeAll();

    /**
     * safly close a closeable
     * 
     * @param closeable
     */
    void ensureClose(Closeable closeable);

    /**
     * see if the server is stopped
     * 
     * @return
     */
    boolean getIsStopped();

    /**
     * stop everything
     */
    void stop();

    /**
     * get chat reference
     * 
     * @return
     */
    Chatserver getChat();

}
