package project.helpify.helpifyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;
    private TextView textResetPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth; //Firebase object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignin);
        textResetPassword = (TextView) findViewById(R.id.textResetPassword);

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }


        //Text in forms centered
        editTextEmail.setGravity(Gravity.CENTER);
        editTextPassword.setGravity(Gravity.CENTER);



        //FONTS
        try{

            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Nirmala.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),"fonts/NirmalaB.ttf");
            Typeface custom_font_slim = Typeface.createFromAsset(getAssets(),"fonts/NirmalaS.ttf");



            editTextEmail.setTypeface(custom_font_slim);
            editTextPassword.setTypeface(custom_font_slim);
            buttonRegister.setTypeface(custom_font_bold);
            textViewSignIn.setTypeface(custom_font);
            textResetPassword.setTypeface(custom_font);
            textResetPassword.setTypeface(custom_font);



        }catch(Exception exc){
            System.out.println("Fontidega jama ");
        }

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
        textResetPassword.setOnClickListener(this);
    }

    private void registerUser() {
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


        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        // USER CREATION
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //CHECKING IF REGISTRATION WAS SUCCESSFUL
                        if (task.isSuccessful()) {
                            //finish current activity and start profile
                            SharedPreferences pref =
                                    getApplicationContext().getSharedPreferences("NewUserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("newUser", "true");
                            editor.commit();
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            Toast.makeText(MainActivity.this, "Siin peaks suunama", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong, try again :(", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }
        if (view == textViewSignIn) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == textResetPassword) {
            finish();
            startActivity(new Intent(this, ResetPasswordActivity.class));
        }
    }
}