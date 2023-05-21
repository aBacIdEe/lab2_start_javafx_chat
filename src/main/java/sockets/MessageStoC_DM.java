package sockets;

public class MessageStoC_DM extends Message{
    public String sender;
    public String msg;
    public byte[] pfp;

    public MessageStoC_DM(String sentBy, String message, byte[] pic) {
        sender = sentBy;
        msg = message;
        pfp = pic;
    } 

    public String toString() {
        return "DM from " + sender + ": " + msg;
    }
    
}
