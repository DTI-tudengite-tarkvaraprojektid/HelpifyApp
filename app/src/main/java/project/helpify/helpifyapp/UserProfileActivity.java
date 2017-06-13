package project.helpify.helpifyapp;

import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import java.util.Date;
import java.util.Locale;



/**
 * Created by Mariam on 12.06.2017.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private EditText editTextName;
    private EditText editTextQuest;
    private Button buttonSaveUserData;
    private EditText questDate;
    private DatabaseReference mDatabase;
    private ImageButton buttonBack;
    private EditText questEndDate;
    private Button mSkills;
    private TextView mSelectedSkills;

    private String[] listSkills;
    private boolean[] checkedSkills;
    private ArrayList<Integer> mUserSkills = new ArrayList<>();
    private int count;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object
        FirebaseUser user = firebaseAuth.getCurrentUser();


        //Text in forms centered
        EditText t = (EditText) findViewById(R.id.editTextName);
        t.setGravity(Gravity.CENTER);
        EditText p = (EditText) findViewById(R.id.editTextQuest);
        p.setGravity(Gravity.CENTER);

        mSkills = (Button) findViewById(R.id.SkillsSelect);

        listSkills = getResources().getStringArray(R.array.skills_list);
        checkedSkills = new boolean[listSkills.length];

        mSelectedSkills = (TextView) findViewById(R.id.SkillsView);

        https://github.com/codingdemos/MultichoiceTutorial/blob/master/app/src/main/java/com/example/multichoicetutorial/MainActivity.java
        mSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(UserProfileActivity.this, R.style.AlertDialogCustom));
                mBuilder.setTitle("   Choose skills");
                mBuilder.setMultiChoiceItems(listSkills, checkedSkills, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            mUserSkills.add(which);
                        } else {
                            mUserSkills.remove(Integer.valueOf(which));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String skill = "";
                        for(int i = 0; i < mUserSkills.size(); i++){
                            skill = skill + listSkills[mUserSkills.get(i)];
                            if(i != mUserSkills.size() - 1){
                                skill = skill + ",";
                            }
                        }

                        mSelectedSkills.setText(skill);
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setNeutralButton("  Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkedSkills.length; i++){
                            checkedSkills[i] = false;
                            mUserSkills.clear();
                            mSelectedSkills.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });


        buttonSaveUserData = (Button) findViewById(R.id.buttonSaveUserData);
        buttonBack = (ImageButton) findViewById(R.id.buttonBack);

        questDate = (EditText) findViewById(R.id.questDate);
        questEndDate = (EditText) findViewById(R.id.questEndDate);
        questEndDate.setGravity(Gravity.CENTER);
        questDate.setGravity(Gravity.CENTER);

        buttonBack.setOnClickListener(this);

        buttonSaveUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == buttonSaveUserData) {
                    EditText quest = (EditText) findViewById(R.id.editTextQuest);
                    EditText name = (EditText) findViewById((R.id.editTextName));

                    String user_quest = quest.getText().toString();
                    String user_name = name.getText().toString();
                    String start_date = questDate.getText().toString();
                    String end_date = questEndDate.getText().toString();


                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    //http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
                    String date_pattern = "dd/MM/yyyy HH:mm";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern, Locale.US);

                    String current_date = simpleDateFormat.format(new Date());


                    try {

                        Date c_Date = simpleDateFormat.parse(current_date);
                        Date d_Date = simpleDateFormat.parse(start_date);
                        Date e_Date = simpleDateFormat.parse(end_date);

                        System.out.println(c_Date.compareTo(d_Date));
                        System.out.println(start_date);

                        //https://stackoverflow.com/questions/23360599/regular-expression-for-dd-mm-yyyy-hhmm
                        boolean pattern_check = start_date.matches("(0[1-9]|1\\d|2\\d|3[01])/(0[1-9]|1[12])/(20)\\d{2}\\s+(0[0-9]|1[0-9]|2[0-3])\\:(0[0-9]|[1-5][0-9])$");

                        System.out.println(pattern_check);

                        if(pattern_check){
                            if(c_Date.compareTo(d_Date) == 0 || c_Date.compareTo(d_Date) < 0){
                                if(e_Date.compareTo(d_Date) > 0){
                                    if(user_quest.equals("") || start_date.equals("") || user_name.equals("")){

                                        Quest new_quest = new Quest("NULL","NULL", userEmail, "NULL", "NULL");
                                        mDatabase.child("quests").child(userId).setValue(new_quest);
                                        Toast.makeText(UserProfileActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                                    } else {

                                        if(mUserSkills.size() == 0){
                                            Toast.makeText(UserProfileActivity.this, "At least one skill must be chosen", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Quest new_user_quest = new Quest(start_date,end_date,userEmail,user_name,user_quest);
                                       
                                        mDatabase.child("quests").child(userId).setValue(new_user_quest);
                                        for(int i = 0; i < mUserSkills.size(); i++){
                                            mDatabase.child("quests").child(userId).child("skill"+i).setValue(listSkills[mUserSkills.get(i)]);
                                        }
                                        Toast.makeText(UserProfileActivity.this, "Quest saved!", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    Toast.makeText(UserProfileActivity.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                                }

                            } else if (c_Date.compareTo(d_Date) > 0) {
                                Toast.makeText(UserProfileActivity.this, "Dates must be in the future", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(UserProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });




    }

    @Override
    public void onClick (View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        ;

    };
}