package dhbw;

import java.util.Observable;

/**
 * Chatserver
 */
public class Chatserver extends Observable {

    private String lastMessage = "";

    public String addMessage(String nickname, String message) {
        this.lastMessage = String.format("%s: %s\n", nickname, message);
        System.out.println(lastMessage);
        return this.lastMessage;
    }

    public void notifyOther() {
        this.setChanged();
        this.notifyObservers(this.lastMessage);
    }

}
