package project.helpify.helpifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Mariam on 15.06.2017.
 */

public class MyOffers extends AppCompatActivity implements View.OnClickListener {
    private ImageButton buttonBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myoffers);

        buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }
}
