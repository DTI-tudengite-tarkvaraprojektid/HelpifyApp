package project.helpify.helpifyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;
    private EditText editTextName;
    private EditText editTextQuest;
    private Button buttonSaveUserData;
    private EditText questDate;

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

        buttonSaveUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == buttonSaveUserData) {
                    EditText quest = (EditText) findViewById(R.id.editTextQuest);
                    EditText name = (EditText) findViewById((R.id.editTextName));

                    String user_quest = quest.getText().toString();
                    String user_name = name.getText().toString();
                    String start_date = questDate.getText().toString();

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    mDatabase.child("quests").child(userId).child("email").setValue(userEmail);

                    if (user_quest.equals("") || start_date.equals("") || user_name.equals("")) {
                        mDatabase.child("quests").child(userId).child("quest").setValue("NULL");
                        mDatabase.child("quests").child(userId).child("date").setValue("NULL");
                        mDatabase.child("quests").child(userId).child("name").setValue("NULL");
                        Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    } else {
                        mDatabase.child("quests").child(userId).child("quest").setValue(user_quest);
                        mDatabase.child("quests").child(userId).child("name").setValue(user_name);
                        mDatabase.child("quests").child(userId).child("date").setValue(start_date);
                        Toast.makeText(ProfileActivity.this, "Quest saved!", Toast.LENGTH_SHORT).show();
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