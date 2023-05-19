package sockets;

import java.io.ObjectInputStream;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ChatGuiSocketListener implements Runnable {

    private ObjectInputStream socketIn;
    private ChatGuiClient chatGuiClient;
    private String username = null;

    // volatile guarantees that different threads reading the same variable will
    // always see the latest write.
    // volatile variables are not stored in caches.
    volatile boolean appRunning = false;

    public ChatGuiSocketListener(ObjectInputStream socketIn,
            ChatGuiClient chatClient) {
        this.socketIn = socketIn;
        this.chatGuiClient = chatClient;
    }

    private void addMessage(String msg) {
        HBox message = new HBox();
        TextArea text = new TextArea(msg);
        text.setEditable(false);
        message.getChildren().add(text);
        chatGuiClient.getMessageArea().getChildren().add(message);
        chatGuiClient.getScrollPane().applyCss();
        chatGuiClient.getScrollPane().layout();
        chatGuiClient.getScrollPane().setVvalue(1);
    }

    private void processWelcomeMessage(MessageStoC_Welcome m) {
        String user = m.userName;

        if (user.equals(this.username)) {
            Platform.runLater(() -> {
                chatGuiClient.getStage().setTitle("Chatter - " + username);
                chatGuiClient.getTextInput().setEditable(true);
                chatGuiClient.getSendButton().setDisable(false);
                // chatGuiClient.getMessageArea().appendText("Welcome to the chat, " + username + "\n");
                addMessage("Welcome to the chat, " + username + "\n");
            });
        }

        else {
            Platform.runLater(() -> {
                // chatGuiClient.getMessageArea().appendText(m.userName + " joined the chat!\n");
                addMessage(m.userName + " joined the chat!\n");

                chatGuiClient.getNames().add(m.userName);
                chatGuiClient.sendMessage(new MessageCtoS_UpdateList(m.userName));
            });
        }
    }

    private void processChatMessage(MessageStoC_Chat m) {
        Platform.runLater(() -> {
            // chatGuiClient.getMessageArea().appendText(m.userName + ": " + m.msg + "\n");
            HBox message = new HBox();
            TextArea text = new TextArea(m.userName + ": " + m.msg + "\n");
            text.setEditable(false);
            text.setWrapText(true);
            message.getChildren().addAll(new ImageView(), text);
            chatGuiClient.getMessageArea().getChildren().add(message);
            chatGuiClient.getScrollPane().applyCss();
            chatGuiClient.getScrollPane().layout();
            chatGuiClient.getScrollPane().setVvalue(1);
        });
    }

    private void processUpdateListMessage(MessageStoC_AddToList m) {
        Platform.runLater(() -> {
            chatGuiClient.getNames().add(m.userName);
        });
    }

    private void processRemoveFromListMessage(MessageStoC_RemoveFromList m) {
        Platform.runLater(() -> {
            for (int i = 0; i < chatGuiClient.getNames().size(); i++) {
                if (chatGuiClient.getNames().get(i).equals(m.userName)) {
                    chatGuiClient.getNames().remove(i);
                    break;
                }
            }
        });
    }

    private void processExitMessage(MessageStoC_Exit m) {
        Platform.runLater(() -> {
            // chatGuiClient.getMessageArea().appendText(m.userName + " has left the chat!\n");
            addMessage(m.userName + " has left the chat!\n");
        });
    }

    public void run() {
        try {
            appRunning = true;

            // Ask the gui to show the username dialog and update username
            // getName launches a modal, therefore needs to run in the JavaFx thread, hence
            // wrapping this with run later
            // save the username for later comparison in processWelcomeMessage
            // Send to the server

            Platform.runLater(() -> {
                this.username = getName();
                chatGuiClient.sendMessage(new MessageCtoS_Join(username));
            });

            // If the user closes the UI window in ChatGuiClient, appRunning is changed to
            // false to exit this loop.
            while (appRunning) {
                Message msg = (Message) socketIn.readObject();

                if (msg instanceof MessageStoC_Welcome) {
                    processWelcomeMessage((MessageStoC_Welcome) msg);
                }else if (msg instanceof MessageStoC_AddToList){
                    processUpdateListMessage((MessageStoC_AddToList) msg);
                } else if (msg instanceof MessageStoC_RemoveFromList) {
                    processRemoveFromListMessage((MessageStoC_RemoveFromList) msg);
                } else if (msg instanceof MessageStoC_Chat) {
                    processChatMessage((MessageStoC_Chat) msg);
                } else if (msg instanceof MessageStoC_Exit) {
                    processExitMessage((MessageStoC_Exit) msg);
                } else {
                    System.out.println("Unhandled message type: " + msg.getClass());
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally {
            System.out.println("Client Listener exiting");
        }
    }

    private String getName() {
        String username = "";
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Enter Chat Name");
        nameDialog.setHeaderText("Please enter your username.");
        nameDialog.setContentText("Name: ");

        while (username.equals("")) {
            Optional<String> name = nameDialog.showAndWait();
            if (!name.isPresent() || name.get().trim().equals(""))
                nameDialog.setHeaderText("You must enter a nonempty name: ");
            else
                username = name.get().trim();
        }
        return username;
    }

}
