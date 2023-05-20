package sockets;

public class MessageStoC_Chat extends Message {
    public String userName;
    public String msg;
    public byte[] pfp;

    public MessageStoC_Chat(String userName, String msg, byte[] pfp) {
        this.userName = userName;
        this.msg = msg;
        this.pfp = pfp;
    }

    public String toString() {
        return "Chat Message from " + userName + ": " + msg;
    }
}