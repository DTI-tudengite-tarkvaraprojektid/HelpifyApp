package project.helpify.helpifyapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by User on 15-Jun-17.
 */
@IgnoreExtraProperties
public class Chat {

 //  public String chatroom;
   public String user;
   public String message;
   public String reciever;


  /*  public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }*/

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Chat(String message,String user, String reciever) {
        this.user = user;
        this.message = message;
        this.reciever = reciever;
    }



}
