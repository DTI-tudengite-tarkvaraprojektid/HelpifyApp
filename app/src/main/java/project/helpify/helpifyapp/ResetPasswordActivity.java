package project.helpify.helpifyapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static project.helpify.helpifyapp.R.layout.activity_resetpassword;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView forgotpasswordtext;
    private TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_resetpassword);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        forgotpasswordtext = (TextView) findViewById(R.id.forgotpasswordtext);
        forgot = (TextView) findViewById(R.id.forgot);


        //FONTS
        try{

            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Nirmala.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),"fonts/NirmalaB.ttf");
            Typeface custom_font_slim = Typeface.createFromAsset(getAssets(),"fonts/NirmalaS.ttf");



            inputEmail.setTypeface(custom_font_slim);
            btnReset.setTypeface(custom_font_bold);
            btnBack.setTypeface(custom_font_bold);
            forgot.setTypeface(custom_font);
            forgotpasswordtext.setTypeface(custom_font);




        }catch(Exception exc){
            System.out.println("Fontidega jama ");
        }

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (v == btnBack) {
                    finish();
                    Intent i = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}