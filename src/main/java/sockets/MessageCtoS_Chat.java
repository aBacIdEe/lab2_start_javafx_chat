package sockets;

public class MessageCtoS_Chat extends Message {
    public String msg;
    public byte[] pfp;

    public MessageCtoS_Chat(String msg, byte[] pfp) {
        this.msg = msg;
        this.pfp = pfp;
    }
    
}
