package project.helpify.helpifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Mariam on 15.06.2017.
 */

public class MyOffers extends AppCompatActivity implements View.OnClickListener {
    private ImageButton buttonBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myoffers);

        final ListView mListView = (ListView) findViewById(R.id.retrieved_users);


        final ArrayList<String> users = new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference().child("quests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            final String email = (String) snapshot.child("email").getValue();
                            String current_user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                            if (email.equals(current_user)) {
                                FirebaseDatabase.getInstance().getReference().child("quests").child(key)
                                        .child("accepted_by").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                            String accepted_users = (String) snapshot1.getValue();

                                            if (accepted_users != null) {
                                                users.add(accepted_users);
                                                System.out.println(users);
                                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                                        (getApplicationContext(), android.R.layout.simple_list_item_1, users);

                                                mListView.setAdapter(arrayAdapter);
                                                //    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

    }

    //final ArrayList<String> users = new ArrayList<String>();


    @Override
    public void onClick(View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }
}
