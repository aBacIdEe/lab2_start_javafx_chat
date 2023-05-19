package sockets;

public class MessageCtoS_DM extends Message{
    public String target;
    public String msg;

    public MessageCtoS_DM(String client, String message) {
        target = client;
        msg = message;
    }
    
}
