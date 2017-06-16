package project.helpify.helpifyapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by User on 15-Jun-17.
 */
@IgnoreExtraProperties
public class Chat {

    String user;
    String message;
    String receiver;

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Chat(String message, String user, String receiver) {
        this.user = user;
        this.message = message;
        this.receiver = receiver;
    }


}
