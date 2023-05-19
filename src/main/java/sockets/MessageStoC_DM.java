package sockets;

public class MessageStoC_DM extends Message{
    public String sender;
    public String msg;

    public MessageStoC_DM(String sentBy, String message) {
        sender = sentBy;
        msg = message;
    } 
    
}
