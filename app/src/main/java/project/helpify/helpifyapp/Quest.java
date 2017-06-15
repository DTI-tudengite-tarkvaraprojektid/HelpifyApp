package project.helpify.helpifyapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by AASA on 09.06.2017.
 */

@IgnoreExtraProperties
class Quest {
    String startDate;
    String endDate;
    String email;
    String name;
    String quest;

    public Quest() {

    }

    public Quest(String startDate, String endDate, String email, String name, String quest) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.email = email;
        this.name = name;
        this.quest = quest;
    }


}
