package project.helpify.helpifyapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

/**
 * Created by User on 06-Jun-17.
 */
@IgnoreExtraProperties
public class User {

    String email;
    Double latitude;
    Double longitude;
    Boolean isOnline;
    Boolean isHidden;
    Long timestamp;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, Double latitude, Double longitude, Boolean isOnline) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
    }

    public User(String email, Double latitude, Double longitude, Boolean isOnline, Boolean isHidden) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOnline = isOnline;
        this.isHidden = isHidden;
    }


    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    Long getTimestampLong() {
        return timestamp;
    }

    void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
