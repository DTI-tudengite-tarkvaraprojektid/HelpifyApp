package project.helpify.helpifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Mariam on 12.06.2017.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private EditText editTextName;
    private EditText editTextQuest;
    private Button buttonSaveUserData;
    private EditText questDate;
    private DatabaseReference mDatabase;
    private ImageButton buttonBack;
    private EditText questEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //Text in forms centered
        EditText t = (EditText) findViewById(R.id.editTextName);
        t.setGravity(Gravity.CENTER);
        EditText p = (EditText) findViewById(R.id.editTextQuest);
        p.setGravity(Gravity.CENTER);

        buttonSaveUserData = (Button) findViewById(R.id.buttonSaveUserData);
        buttonBack = (ImageButton) findViewById(R.id.buttonBack);

        questDate = (EditText) findViewById(R.id.questDate);
        questEndDate = (EditText) findViewById(R.id.questEndDate);
        questEndDate.setGravity(Gravity.CENTER);
        questDate.setGravity(Gravity.CENTER);

        buttonBack.setOnClickListener(this);

        buttonSaveUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == buttonSaveUserData) {
                    EditText quest = (EditText) findViewById(R.id.editTextQuest);
                    EditText name = (EditText) findViewById((R.id.editTextName));

                    String user_quest = quest.getText().toString();
                    String user_name = name.getText().toString();
                    String start_date = questDate.getText().toString();
                    String end_date = questEndDate.getText().toString();

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    //http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
                    String date_pattern = "dd/MM/yyyy HH:mm";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern, Locale.US);

                    String current_date = simpleDateFormat.format(new Date());


                    try {

                        Date c_Date = simpleDateFormat.parse(current_date);
                        Date d_Date = simpleDateFormat.parse(start_date);
                        Date e_Date = simpleDateFormat.parse(end_date);

                        System.out.println(c_Date.compareTo(d_Date));
                        System.out.println(start_date);

                        //https://stackoverflow.com/questions/23360599/regular-expression-for-dd-mm-yyyy-hhmm
                        boolean pattern_check = start_date.matches("(0[1-9]|1\\d|2\\d|3[01])/(0[1-9]|1[12])/(20)\\d{2}\\s+(0[0-9]|1[0-9]|2[0-3])\\:(0[0-9]|[1-5][0-9])$");

                        System.out.println(pattern_check);

                        if(pattern_check){
                            if(c_Date.compareTo(d_Date) == 0 || c_Date.compareTo(d_Date) < 0){
                                if(e_Date.compareTo(d_Date) > 0){
                                    if(user_quest.equals("") || start_date.equals("") || user_name.equals("")){

                                        Quest new_quest = new Quest("NULL","NULL", userEmail, "NULL", "NULL");
                                        mDatabase.child("quests").child(userId).setValue(new_quest);
                                        Toast.makeText(UserProfileActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                                    } else {

                                        Quest new_user_quest = new Quest(start_date,end_date,userEmail,user_name,user_quest);
                                        mDatabase.child("quests").child(userId).setValue(new_user_quest);
                                        Toast.makeText(UserProfileActivity.this, "Quest saved!", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    Toast.makeText(UserProfileActivity.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                                }

                            } else if(c_Date.compareTo(d_Date) > 0){
                                Toast.makeText(UserProfileActivity.this, "Dates must be in the future", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(UserProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == buttonBack){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }
}
