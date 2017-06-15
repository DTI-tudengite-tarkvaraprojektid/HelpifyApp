package project.helpify.helpifyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textResetPassword;

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
        EditText t = (EditText) findViewById(R.id.editTextEmail);
        t.setGravity(Gravity.CENTER);
        EditText p = (EditText) findViewById(R.id.editTextPassword);
        p.setGravity(Gravity.CENTER);

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
