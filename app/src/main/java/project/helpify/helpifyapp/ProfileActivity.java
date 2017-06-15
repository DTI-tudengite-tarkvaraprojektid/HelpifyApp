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


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonProfile;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        openGoogleMapsLayout();
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonProfile = (Button) findViewById(R.id.buttonProfile);


        buttonLogout.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);


        //Check if user is not signed in
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //Create user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail.setText("Welcome " + user.getEmail());


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
        if (view == buttonProfile) {
            finish();
            startActivity(new Intent(this, UserProfileActivity.class));
        }


    }
}