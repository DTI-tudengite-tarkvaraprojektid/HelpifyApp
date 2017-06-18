package project.helpify.helpifyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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


        String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("quests").child(current_user_id)
                .child("accepted_by").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String user = (String) dataSnapshot.getValue();
                    users.add(user);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (MyOffers.this, android.R.layout.simple_list_item_1, users);

                    mListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String user_removed = (String) dataSnapshot.getValue();
                    users.remove(user_removed);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (MyOffers.this, android.R.layout.simple_list_item_1, users);

                    mListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itempos = position;
                final String user = (String) mListView.getItemAtPosition(itempos);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyOffers.this);
                mBuilder.setMessage(user)
                        .setTitle("Accept this users help?");

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceptUserHelp(user);
                    }
                });

                mBuilder.setNegativeButton("DECLINE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        declineUserHelp(user);
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

    }

    private void acceptUserHelp(final String user) {

        final String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("quests").child(current_user)
                .child("accepted_by").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String removed_keys = snapshot.getKey();
                        String removed_users = (String) snapshot.getValue();
                        if (!removed_users.equals(user)) {
                            FirebaseDatabase.getInstance().getReference().child("quests")
                                    .child(current_user).child("accepted_by").child(removed_keys).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void declineUserHelp(final String user) {

        final String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("quests").child(current_user)
                .child("accepted_by").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key_to_remove = snapshot.getKey();
                        String user_to_remove = (String) snapshot.getValue();
                        if (user_to_remove.equals(user)) {
                            FirebaseDatabase.getInstance().getReference().child("quests")
                                    .child(current_user).child("accepted_by")
                                    .child(key_to_remove).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));

    }


}
