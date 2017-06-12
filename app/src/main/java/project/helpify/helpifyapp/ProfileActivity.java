package project.helpify.helpifyapp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;
    private EditText editTextName;
    private EditText editTextQuest;
    private Button buttonSaveUserData;
    private EditText questDate;
    private EditText questEndDate;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        openGoogleMapsLayout();
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);


        buttonLogout.setOnClickListener(this);

        //Check if user is not signed in
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //Create user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail.setText("Welcome " + user.getEmail());

        //Text in forms centered
        EditText t = (EditText) findViewById(R.id.editTextName);
        t.setGravity(Gravity.CENTER);
        EditText p = (EditText) findViewById(R.id.editTextQuest);
        p.setGravity(Gravity.CENTER);

        buttonSaveUserData = (Button) findViewById(R.id.buttonSaveUserData);

        questDate = (EditText) findViewById(R.id.questDate);
        questDate.setGravity(Gravity.CENTER);

        questEndDate = (EditText) findViewById(R.id.questEndDate);
        questEndDate.setGravity(Gravity.CENTER);

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

                        if(pattern_check){
                            if(c_Date.compareTo(d_Date) == 0 || c_Date.compareTo(d_Date) < 0){
                                if(e_Date.compareTo(d_Date) > 0){
                                    if(user_quest.equals("") || start_date.equals("") || user_name.equals("")){

                                        Quest new_quest = new Quest("NULL","NULL", userEmail, "NULL", "NULL");
                                        mDatabase.child("quests").child(userId).setValue(new_quest);
                                        Toast.makeText(ProfileActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                                    } else {

                                        Quest new_user_quest = new Quest(start_date,end_date,userEmail,user_name,user_quest);
                                        mDatabase.child("quests").child(userId).setValue(new_user_quest);
                                        Toast.makeText(ProfileActivity.this, "Quest saved!", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    Toast.makeText(ProfileActivity.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                                }

                            } else if(c_Date.compareTo(d_Date) > 0){
                                Toast.makeText(ProfileActivity.this, "Dates must be in the future", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }


    private void openGoogleMapsLayout() {
        Button gotoGoogleMapsBtn = (Button) findViewById(R.id.buttonMaps);
        gotoGoogleMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout) {
            User user = new User();
            String userId = firebaseAuth.getCurrentUser().getUid();

            user.isOnline = false;

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(userId).child("isOnline").setValue(user.isOnline);

            firebaseAuth.signOut();

            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

    }
}