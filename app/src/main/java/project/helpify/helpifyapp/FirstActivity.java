package project.helpify.helpifyapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Mariam on 12.06.2017.
 */

public class FirstActivity  extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewWelcome;
    private Button buttonRegistration;
    private Button buttonLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        buttonRegistration  = (Button) findViewById(R.id.buttonRegistration);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegistration.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);






    }
    @Override
    public void onClick(View v) {
        if(v==buttonRegistration){
            finish();
           startActivity(new Intent(this, MainActivity.class));
        }
        if (v==buttonLogin){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}
