package dhbw;

import java.util.Observable;

/**
 * Chatserver
 */
public class Chatserver extends Observable {

    private String lastMessage = "";

    public void addMessage(String nickname, String message) {
        this.addMessage(nickname, message, null);
    }

    public void addMessage(String nickname, String message, BeforeSend beforeSend) {
        this.lastMessage = String.format("%s: %s\n", nickname, message);
        System.out.println(lastMessage);
        if (beforeSend != null) {
            beforeSend.before(this.lastMessage);
        }
        this.setChanged();
        this.notifyObservers(this.lastMessage);
    }

}
