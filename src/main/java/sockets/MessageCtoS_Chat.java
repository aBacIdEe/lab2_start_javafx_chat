package sockets;

public class MessageCtoS_Chat extends Message {
    public String msg;
    public String pfp;

    public MessageCtoS_Chat(String msg, String pfp) {
        this.msg = msg;
        this.pfp = pfp;
    }
    
}
