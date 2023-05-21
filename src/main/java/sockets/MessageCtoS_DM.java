package sockets;

public class MessageCtoS_DM extends Message{
    public String target;
    public String msg;
    public byte[] pfp;

    public MessageCtoS_DM(String client, String message, byte[] pic) {
        target = client;
        msg = message;
        pfp = pic;
    }
    
}
