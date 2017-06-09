package project.helpify.helpifyapp;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by AASA on 09.06.2017.
 */

@IgnoreExtraProperties
class Quest {
    public String date;
    public String email;
    public String name;
    public String quest;

    public Quest(){

    }

    public Quest(String date, String email, String name, String quest){
        this.date = date;
        this.email = email;
        this.name = name;
        this.quest = quest;
    }

}
