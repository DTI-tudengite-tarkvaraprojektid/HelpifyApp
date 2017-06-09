package project.helpify.helpifyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;
    private EditText editTextName;
    private EditText editTextQuest;
    private Button buttonSaveUserData;

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
        if(firebaseAuth.getCurrentUser() == null){
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

        buttonSaveUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == buttonSaveUserData){
                    EditText quest = (EditText) findViewById(R.id.editTextQuest);
                    EditText name = (EditText) findViewById((R.id.editTextName));

                    String user_quest = quest.getText().toString();
                    String user_name = name.getText().toString();

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    if(user_quest.equals("") || user_quest == null){
                        mDatabase.child("quests").child(userId).child("quest").setValue("NULL");
                    } else {
                        mDatabase.child("quests").child(userId).child("quest").setValue(user_quest);
                    }

                    if(user_name.equals("") || user_name == null){
                        mDatabase.child("quests").child(userId).child("name").setValue("NULL");
                    } else {
                       // mDatabase.child("users").child(userId).child("Quest").setValue(user_quest);
                        mDatabase.child("quests").child(userId).child("name").setValue(user_name);
                    }
            }

            }
        });

    }


    private void openGoogleMapsLayout(){
        Button gotoGoogleMapsBtn = (Button) findViewById(R.id.buttonMaps);
        gotoGoogleMapsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}