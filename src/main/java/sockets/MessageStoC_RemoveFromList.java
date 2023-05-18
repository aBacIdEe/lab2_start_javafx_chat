package sockets;

public class MessageStoC_RemoveFromList extends Message {
    public String userName;

    public MessageStoC_RemoveFromList(String n) {
        userName = n;
    }
}
