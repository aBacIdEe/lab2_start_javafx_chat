package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.List;

public class ChatServerSocketListener  implements Runnable {
    private ClientConnectionData client;
    private List<ClientConnectionData> clientList;

    public ChatServerSocketListener(ClientConnectionData client, List<ClientConnectionData> clientList) {
        this.client = client;
        this.clientList = clientList;
    }

    private void processChatMessage(MessageCtoS_Chat m) {
        System.out.println("Chat received from " + client.getUserName() + " - broadcasting");
        broadcast(new MessageStoC_Chat(client.getUserName(), m.msg, m.pfp), null);
    }

    private void processUpdateListMessage(MessageCtoS_UpdateList m) {
        broadcast(new MessageStoC_AddToList(client.getUserName()), client);
    }

    private void processDM(MessageCtoS_DM m) {
        directMessage(new MessageStoC_DM(client.getUserName(), m.msg, m.pfp), m.target, client.getUserName());
    }

    /**
     * Broadcasts a message to all clients connected to the server.
     */
    public void broadcast(Message m, ClientConnectionData skipClient) {
        try {
            System.out.println("broadcasting: " + m);
            for (ClientConnectionData c : clientList){
                // if c equals skipClient, then c.
                // or if c hasn't set a userName yet (still joining the server)
                if ((c != skipClient) && (c.getUserName()!= null)){
                    c.getOut().writeObject(m);
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }        
    }

    public void directMessage(Message m, String target, String target2) {
        try {
            for (ClientConnectionData c: clientList) {
                if (((c.getUserName().equals(target)) || c.getUserName().equals(target2)) && c.getUserName() != null) {
                    c.getOut().writeObject(m);
                }
            }
        } catch (Exception ex) {
            System.out.println("directMessage caught exception: " + ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = client.getInput();

            MessageCtoS_Join joinMessage = (MessageCtoS_Join)in.readObject();
            client.setUserName(joinMessage.userName);

            // Broadcast the welcome back to the client that joined. 
            // Their UI can decide what to do with the welcome message.
            broadcast(new MessageStoC_Welcome(joinMessage.userName), null);
            
            while (true) {
                Message msg = (Message) in.readObject();
                if (msg instanceof MessageCtoS_Quit) {
                    break;
                }
                else if (msg instanceof MessageCtoS_UpdateList) {
                    processUpdateListMessage((MessageCtoS_UpdateList) msg);
                }
                else if (msg instanceof MessageCtoS_DM) {
                    processDM((MessageCtoS_DM) msg);
                }
                else if (msg instanceof MessageCtoS_Chat) {
                    processChatMessage((MessageCtoS_Chat) msg);
                }
                else {
                    System.out.println("Unhandled message type: " + msg.getClass());
                }
            }
        } catch (Exception ex) {
            if (ex instanceof SocketException) {
                System.out.println("Caught socket ex for " + 
                    client.getName());
            } else {
                System.out.println(ex);
                ex.printStackTrace();
            }
        } finally {
            //Remove client from clientList
            clientList.remove(client);
            broadcast(new MessageStoC_RemoveFromList(client.getUserName()), client);

            // Notify everyone that the user left.
            broadcast(new MessageStoC_Exit(client.getUserName()), client);

            try {
                client.getSocket().close();
            } catch (IOException ex) {}
        }
    }
        
}
