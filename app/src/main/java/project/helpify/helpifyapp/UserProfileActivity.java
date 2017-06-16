package project.helpify.helpifyapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



/**
 * Created by Mariam on 12.06.2017.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private EditText editTextName;
    private Button buttonSaveUserData;
    private DatabaseReference mDatabase;
    private ImageButton buttonBack;
    private ProgressDialog progressBar;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object
        FirebaseUser user = firebaseAuth.getCurrentUser();



        buttonBack = (ImageButton) findViewById(R.id.buttonBack);


        buttonBack.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = firebaseAuth.getCurrentUser().getUid();
        String userEmail = firebaseAuth.getCurrentUser().getEmail();



    }

    @Override
    public void onClick(View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }}}
