package sockets;

public class MessageStoC_UpdateList extends Message {
    public String userName;

    public MessageStoC_UpdateList(String n) {
        userName = n;
    }
}
