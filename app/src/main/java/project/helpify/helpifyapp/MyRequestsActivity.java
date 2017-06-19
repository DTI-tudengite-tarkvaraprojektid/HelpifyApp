package project.helpify.helpifyapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;



/**
 * Created by Mariam on 12.06.2017.
 */

public class MyRequestsActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private Button buttonSaveUserData;
    private DatabaseReference mDatabase;
    private ImageButton buttonBack;
    private TextView mSelectedSkills;
    private String[] listSkills;
    private boolean[] checkedSkills;
    private ArrayList<Integer> mUserSkills = new ArrayList<>();
    private TextView tv;
    private TextView editTextDate;
    private String date;
    private Button SkillsSelect;
    private TextView SkillsView;



    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrequests);
        firebaseAuth = FirebaseAuth.getInstance();  //firebase object


        //Text in forms centered
        EditText t = (EditText) findViewById(R.id.editTextName);
        t.setGravity(Gravity.CENTER);
        EditText p = (EditText) findViewById(R.id.editTextQuest);
        p.setGravity(Gravity.CENTER);


        TextView textViewDataChange = (TextView) findViewById(R.id.textViewDataChange);

        TextView fontdate = (TextView) findViewById(R.id.editTextDate);
        TextView fonttime = (TextView) findViewById(R.id.tv);
        Button SkillsSelect = (Button) findViewById(R.id.SkillsSelect);
        Button buttonSaveUserDatas = (Button) findViewById(R.id.buttonSaveUserData);
        Button SkillsSelectButton = (Button) findViewById(R.id.SkillsSelect);

        //FONTS
        try{

            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Nirmala.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),"fonts/NirmalaB.ttf");
            Typeface custom_font_slim = Typeface.createFromAsset(getAssets(),"fonts/NirmalaS.ttf");



            textViewDataChange.setTypeface(custom_font_bold);
            t.setTypeface(custom_font_slim);
            p.setTypeface(custom_font_slim);
            fontdate.setTypeface(custom_font_slim);
            fonttime.setTypeface(custom_font_slim);
            SkillsSelect.setTypeface(custom_font_bold);
            buttonSaveUserDatas.setTypeface(custom_font_bold);
            SkillsSelectButton.setTypeface(custom_font_bold);




        }catch(Exception exc){
            Toast.makeText(MyRequestsActivity.this, " ", Toast.LENGTH_SHORT).show();
        }


        Button mSkills = (Button) findViewById(R.id.SkillsSelect);

        listSkills = getResources().getStringArray(R.array.skills_list);
        checkedSkills = new boolean[listSkills.length];



        // https://github.com/codingdemos/MultichoiceTutorial/blob/master/app/src/main/java/com/example/multichoicetutorial/MainActivity.java
        mSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MyRequestsActivity.this, R.style.AlertDialogCustom));
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

        editTextDate  = (TextView) findViewById(R.id.editTextDate);
        tv =(TextView) findViewById(R.id.tv);
        tv.setGravity(Gravity.CENTER);
        editTextDate.setGravity(Gravity.CENTER);

        buttonBack.setOnClickListener(this);


        tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");

            }




        });





        //DATE PICKER
        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MyRequestsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;

                            date = stringifyNumber(selectedday) + "/" + stringifyNumber(selectedmonth) + "/" + selectedyear;
                            editTextDate.setText(date);


                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        //**

         buttonSaveUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == buttonSaveUserData) {
                    EditText quest = (EditText) findViewById(R.id.editTextQuest);
                    EditText name = (EditText) findViewById((R.id.editTextName));
                    String user_quest = quest.getText().toString();
                    String user_name = name.getText().toString();
                    String time = tv.getText().toString();
                    String dateTime = date+" "+time;
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    //http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
                    String date_pattern = "dd/MM/yyyy HH:mm";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern, Locale.US);
                    String current_date = simpleDateFormat.format(new Date());
                    try {
                        Date c_Date = simpleDateFormat.parse(current_date);
                        Date d_Date = simpleDateFormat.parse(current_date);
                        Date e_Date = simpleDateFormat.parse(dateTime);
                        System.out.println(e_Date);

                        // System.out.println(c_Date.compareTo(d_Date));
                        //https://stackoverflow.com/questions/23360599/regular-expression-for-dd-mm-yyyy-hhmm
                        // boolean pattern_check = dateTime.matches("(0[1-9]|1\\d|2\\d|3[01])/(0[1-9]|1[12])/(20)\\d{2}\\s+(0[0-9]|1[0-9]|2[0-3])\\:(0[0-9]|[1-5][0-9])$");
                        if(e_Date.compareTo(d_Date)>0){
                            if(c_Date.compareTo(d_Date) == 0 || c_Date.compareTo(d_Date) < 0){
                                if(e_Date.compareTo(d_Date) > 0){
                                    if(user_quest.equals("") || dateTime.equals("") || user_name.equals("")){
                                        Quest new_quest = new Quest("NULL","NULL", userEmail, "NULL", "NULL");
                                        mDatabase.child("quests").child(userId).setValue(new_quest);
                                        Toast.makeText(MyRequestsActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(mUserSkills.size() == 0){
                                            Toast.makeText(MyRequestsActivity.this, "At least one skill must be chosen", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Quest new_user_quest = new Quest(current_date,dateTime,userEmail,user_name,user_quest);
                                        mDatabase.child("quests").child(userId).setValue(new_user_quest);
                                        for(int i = 0; i < mUserSkills.size(); i++){
                                            mDatabase.child("quests").child(userId).child("skill"+i).setValue(listSkills[mUserSkills.get(i)]);
                                        }
                                        mDatabase.child("quests").child(userId).child("accepted").setValue(false);
                                        Toast.makeText(MyRequestsActivity.this, "Quest saved!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        } else {
                            Toast.makeText(MyRequestsActivity.this, "Quest must expire in the future.", Toast.LENGTH_SHORT).show();
                            System.out.println(dateTime);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(MyRequestsActivity.this, "Wrong date format.", Toast.LENGTH_SHORT).show();
                        System.out.println();
                    }
                }
            }
        });
    }

    private String stringifyNumber(Integer number) {
        if (number < 10) {
            return "0" + number.toString();
        }
        return number.toString();
    }

    @Override
    public void onClick (View v) {
        if (v == buttonBack) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));

    };
}
