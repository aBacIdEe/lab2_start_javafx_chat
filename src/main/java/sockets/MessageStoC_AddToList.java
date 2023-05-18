package sockets;

public class MessageStoC_AddToList extends Message {
    public String userName;

    public MessageStoC_AddToList(String n) {
        userName = n;
    }
}
