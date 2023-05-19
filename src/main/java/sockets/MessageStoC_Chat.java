package sockets;

public class MessageStoC_Chat extends Message {
    public String userName;
    public String msg;
    public String img;

    public MessageStoC_Chat(String userName, String msg, String img) {
        this.userName = userName;
        this.msg = msg;
        this.img = img;
    }

    public String toString() {
        return "Chat Message from " + userName + ": " + msg;
    }
}