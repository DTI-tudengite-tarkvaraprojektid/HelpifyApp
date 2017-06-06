package project.helpify.helpifyapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;
import java.security.Timestamp;
import java.util.Map;

/**
 * Created by User on 06-Jun-17.
 */
@IgnoreExtraProperties
public  class User {

    public String email;
    public Double latitude;
    public Double longitude;



    public Map<String,Object> mTimestamp;
  public User() {
      // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

    public User(String email, Double latitude, Double longitude, Map<String,Object> timestamp) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        mTimestamp = timestamp;
    }



}
