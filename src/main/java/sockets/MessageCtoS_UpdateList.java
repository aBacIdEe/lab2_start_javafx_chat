package sockets;

public class MessageCtoS_UpdateList extends Message {
    public String userName;

    public MessageCtoS_UpdateList(String n) {
        userName = n;
    }
}
