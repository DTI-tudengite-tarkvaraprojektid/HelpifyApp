package project.helpify.helpifyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textResetPassword;
    private CheckBox checkBox;
    private TextView textViewOrLogIn;
    private TextView QuestStartDateTime;
    private TextView QuestStartDate;

    private FirebaseAuth firebaseAuth;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewSignup = (TextView) findViewById(R.id.textViewSignup);
        textResetPassword = (TextView) findViewById(R.id.textResetPassword);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        textViewOrLogIn = (TextView) findViewById(R.id.textViewOrLogIn);
        QuestStartDateTime = (TextView) findViewById(R.id.QuestStartDateTime);
        QuestStartDate = (TextView) findViewById(R.id.QuestStartDate);

        //FONTS
        try{

            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Nirmala.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),"fonts/NirmalaB.ttf");
            Typeface custom_font_slim = Typeface.createFromAsset(getAssets(),"fonts/NirmalaS.ttf");



            editTextEmail.setTypeface(custom_font_slim);
            editTextPassword.setTypeface(custom_font_slim);
            buttonSignIn.setTypeface(custom_font_bold);
            textViewSignup.setTypeface(custom_font);
            textResetPassword.setTypeface(custom_font);
            checkBox.setTypeface(custom_font);
            textViewOrLogIn.setTypeface(custom_font);
            QuestStartDate.setTypeface(custom_font);
            QuestStartDateTime.setTypeface(custom_font);


        }catch(Exception exc){
            Toast.makeText(LoginActivity.this, "Couldn't load fonts", Toast.LENGTH_SHORT).show();
        }





        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textResetPassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        SpannableString content = new SpannableString("Signup");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewSignup.setText(content);

        //Checking if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        //Text in forms centered
        editTextEmail.setGravity(Gravity.CENTER);
        editTextPassword.setGravity(Gravity.CENTER);

    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //Stops the function from excecuting further.
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //Same for password
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging in, please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            //start the profile activity

                            //  if login was successful then we write to the database

                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                    }
                });
    }

    //AAAA
    //AAAA AAAA
    //AAAA AAAA AAAA
    //AAAA AAAA
    //AAAA
    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == textViewSignup) {
            //If user doesnt't have an excisting account
            finish(); // Close this activity
            startActivity(new Intent(this, MainActivity.class));//Start activity with registration form.
        }
        if (view == textResetPassword) {
            //Password reset page opens here
            finish();
            startActivity(new Intent(this, ResetPasswordActivity.class));
        }
    }
}
