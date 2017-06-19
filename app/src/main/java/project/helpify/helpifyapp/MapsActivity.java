package project.helpify.helpifyapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng lastUserLocation;
    private Boolean startingCameraPosition = false;
    private Boolean drawerUp = false;
    public Boolean isHidden;
    public Boolean newUserCreated = false;
    int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x111;
    // FIREBASE

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    private void getUserIsHidden() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseUser cUser = firebaseAuth.getCurrentUser();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user.email.equals(cUser.getEmail())) {
                                if (user.isHidden == null) {
                                    isHidden = false;
                                    break;
                                } else {
                                    isHidden = user.isHidden;
                                    break;
                                }
                            }
                        }
                        //isHidden = userIsHidden[0];
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setMessage(true, databaseError.toString());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SharedPreferences pref =
                getApplicationContext().getSharedPreferences("NewUserPrefs", MODE_PRIVATE);
        String newUserPref = pref.getString("newUser", null);
        if (newUserPref != null && !newUserCreated) {
            isHidden = false;
            newUserCreated = true;
        }
        getUserIsHidden();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //FONTS
        try {

            Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Nirmala.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(getAssets(), "fonts/NirmalaB.ttf");
            Typeface custom_font_slim = Typeface.createFromAsset(getAssets(), "fonts/NirmalaS.ttf");

            TextView missionName = (TextView) findViewById(R.id.missionName);
            TextView chatBox = (TextView) findViewById(R.id.chatBox);
            Button acceptButton = (Button) findViewById(R.id.acceptButton);
            Button msg_send = (Button) findViewById(R.id.msg_send);
            Button closebutton = (Button) findViewById(R.id.closebutton);
            TextView timeLeftText = (TextView) findViewById(R.id.timeLeftText);
            TextView timeLeft = (TextView) findViewById(R.id.timeLeft);
            TextView uName = (TextView) findViewById(R.id.uName);
            TextView missionTime = (TextView) findViewById(R.id.missionTime);


            missionName.setTypeface(custom_font);
            chatBox.setTypeface(custom_font);
            msg_send.setTypeface(custom_font_bold);
            acceptButton.setTypeface(custom_font_bold);
            closebutton.setTypeface(custom_font_bold);
            timeLeft.setTypeface(custom_font_bold);
            timeLeftText.setTypeface(custom_font_bold);
            uName.setTypeface(custom_font_bold);
            missionTime.setTypeface(custom_font_slim);

        } catch (Exception exc) {
            Toast.makeText(MapsActivity.this, " ", Toast.LENGTH_SHORT).show();
        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        ImageButton ib = (ImageButton) findViewById(R.id.myLocationButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(lastUserLocation)
                        .zoom(14.0f)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        ImageButton disableLocationButton = (ImageButton) findViewById(R.id.disableLocation);
        disableLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseUser cUser = firebaseAuth.getCurrentUser();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    if (user.email.equals(cUser.getEmail())) {
                                        if (user.isHidden == null || !user.isHidden) {
                                            isHidden = true;
                                            mDatabase.child("users").child(cUser.getUid()).child("isHidden").setValue(true);
                                        } else {
                                            isHidden = false;
                                            mDatabase.child("users").child(cUser.getUid()).child("isHidden").setValue(false);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });


        ImageButton messageIcon = (ImageButton) findViewById(R.id.messageRecieve);
        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("quests")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Quest quest = snapshot.getValue(Quest.class);
                                    if (firebaseAuth.getCurrentUser().getEmail().equals(quest.email)) {

                                        generateDrawer(quest.name, quest.email, quest.startDate, quest.endDate, quest.quest);
                                        chat_box.setText(quest.quest + "\n");
                                        showDrawer();

                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });

        Button closeButton = (Button) findViewById(R.id.closebutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDrawer();
            }
        });

        final ImageButton questNotifier = (ImageButton) findViewById(R.id.questNotifier);
        questNotifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == questNotifier) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MyOffers.class));
                }
            }
        });

    }

    private void generateUserMarkers() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user.isHidden == null) {
                                user.isHidden = false;
                            }
                            hasUserTasks(user);
                            hasUserMessages(user);
                            hasUserQuests(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setMessage(true, databaseError.toString());
                    }
                });
    }

    private void hasUserQuests(final User user) {
        FirebaseDatabase.getInstance().getReference().child("quests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            String email = (String) snapshot.child("email").getValue();

                            if (email.equals(firebaseAuth.getCurrentUser().getEmail())) {
                                FirebaseDatabase.getInstance().getReference().child("quests").child(key)
                                        .child("accepted_by").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                            String data = (String) snapshot1.getValue();
                                            System.out.println(data);
                                            if (data != null) {
                                                ImageButton questNotifier = (ImageButton) findViewById(R.id.questNotifier);
                                                questNotifier.setVisibility(View.VISIBLE);
                                                break;
                                            } else {
                                                ImageButton questNotifier = (ImageButton) findViewById(R.id.questNotifier);
                                                questNotifier.setVisibility(View.INVISIBLE);
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                ImageButton questNotifier = (ImageButton) findViewById(R.id.questNotifier);
                                questNotifier.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void hasUserMessages(final User user) {
        FirebaseDatabase.getInstance().getReference().child("chat")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String roomName = snapshot.getKey();

                            FirebaseDatabase.getInstance().getReference().child("chat").child(roomName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                if (snapshot1.child("receiver").getValue().equals(user.email) && firebaseAuth.getCurrentUser().getEmail().equals(snapshot1.child("receiver").getValue())) {
                                                    ImageButton messageIcon = (ImageButton) findViewById(R.id.messageRecieve);
                                                    messageIcon.setVisibility(View.VISIBLE);
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void hasUserTasks(final User user) {

        FirebaseDatabase.getInstance().getReference().child("quests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> quests = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Quest quest = snapshot.getValue(Quest.class);
                            quests.add(quest.email);
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Quest quest = snapshot.getValue(Quest.class);

                            if (user.isOnline && user.isHidden != null && !user.isHidden) {
                                // IF USER HAS ENTERED QUEST, THEN HIS MARKER WILL BE RED, OTHERWISE BLUE
                                if (user.email.equals(quest.email) && quest.quest.equals("NULL")) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email);
                                    break;
                                } else if (!quests.contains(user.email)) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email);
                                    break;
                                } else if (user.email.equals(quest.email) && !quest.quest.equals("NULL")) {
                                    questAfterUserTimestamp(user, quest);
                                    break;
                                } else {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);
                                    break;
                                }
                            } else {
                                questAfterUserTimestamp(user, quest);
                                 break;
                            }
                        }
                    }

                    // TEST

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserIsHidden();
        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generateMap();
                        getUserIsHidden();
                    }
                });
            }
        }, 0, 15000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        Starts activity when a circle is clicked.
         */
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(final Circle circle) {
                if (!circle.getTag().toString().equals("NULL") && !circle.getTag().toString().equals("USER")) {
                    FirebaseDatabase.getInstance().getReference().child("quests")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Quest quest = snapshot.getValue(Quest.class);
                                        if (circle.getTag().toString().equals(quest.email)) {
                                            generateDrawer(quest.name, quest.email, quest.startDate, quest.endDate, quest.quest);
                                            chat_box.setText(quest.quest + "\n");
                                            showDrawer();
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                    showDrawer();
                } else {
                    hideDrawer();
                }
            }

        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setMessage(false);
        generateMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                finish();
                startActivity(getIntent());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        setMessage(true, "No internet connection. Please connect the device.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setMessage(true, "No internet connection. Please connect the device.");
    }

    TextView chat_box;
    EditText msg_input;

    private void append_chat_conversation(DataSnapshot snapshot) {
        Iterator i = snapshot.getChildren().iterator();
        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            String chat_msg_receiver = (String) (((DataSnapshot) i.next()).getValue());
            chat_box.append(chat_user_name + " : " + chat_msg + "\n");
        }
    }

    Map<String, Object> chat = new HashMap<>();
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> msg = new HashMap<>();

    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("chat");
    // mRoot2 -  "chat" child
    DatabaseReference mRoot2;
    // message_root - mRoot2 child
    DatabaseReference message_root;

    String temp_key;

    /* -------------------------DRAWER------------------------------------------------------------*/
    public void generateDrawer(final String name, final String username, String start, final String end, final String description) {
        final TextView uName = (TextView) findViewById(R.id.uName);
        TextView missionTime = (TextView) findViewById(R.id.missionTime);
        TextView missionName = (TextView) findViewById(R.id.missionName);
        chat_box = (TextView) findViewById(R.id.chatBox);
        TextView timeLeft = (TextView) findViewById(R.id.timeLeft);
        msg_input = (EditText) findViewById(R.id.msg_input);

        final Button accept = (Button) findViewById(R.id.acceptButton);
        final Button sendButton = (Button) findViewById(R.id.msg_send);

        mRoot2 = FirebaseDatabase.getInstance().getReference().child("chat").child(name + description.substring(description.length() - 4, description.length()));
        chat_box.setMovementMethod(new ScrollingMovementMethod());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   chat_box.setText("");
                // unique key
                temp_key = mRoot2.push().getKey();
                root.updateChildren(map);
                mRoot2.updateChildren(chat);
                message_root = mRoot2.child(temp_key);
                msg.put("name", firebaseAuth.getCurrentUser().getEmail());
                msg.put("msg", msg_input.getText().toString());
                message_root.updateChildren(msg);
                msg_input.setText(null);
            }
        });

        mRoot2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // unique key
                temp_key = mRoot2.push().getKey();
                root.updateChildren(map);
                mRoot2.updateChildren(chat);

                message_root = mRoot2.child(temp_key);


                msg.put("name", firebaseAuth.getCurrentUser().getEmail());
                msg.put("msg", msg_input.getText().toString());
                msg.put("receiver", username);
                message_root.updateChildren(msg);
                msg_input.setText("");

            }
        });


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mDatabase.child("quests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String email = (String) snapshot.child("email").getValue();
                            String key = snapshot.getKey();
                            String current_user = firebaseAuth.getCurrentUser().getEmail();
                            String current_user_uid = firebaseAuth.getCurrentUser().getUid();
                            String offering_user = uName.getText().toString();

                            final DatabaseReference quest_ref = FirebaseDatabase.getInstance()
                                    .getReference().child("quests").child(key);

                            final DatabaseReference quest_ref2 = FirebaseDatabase.getInstance()
                                    .getReference().child("quests").child(key).child("accepted_by/");

                            if (email.equals(offering_user) && v == accept) {
                                quest_ref.child("accepted").setValue(true);
                                HashMap<String, Object> accepting_user = new HashMap<>();
                                accepting_user.put(current_user_uid, current_user);
                                quest_ref2.updateChildren(accepting_user);
                                v.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        uName.setText(username);
        missionName.setText(name);
        Calendar now = Calendar.getInstance();
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            startDate.setTime(dateFormat.parse(start));
            endDate.setTime(dateFormat.parse(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = stringifyNumber(startDate.get(Calendar.HOUR_OF_DAY)) + ":" + stringifyNumber(startDate.get(Calendar.MINUTE)) +
                " - " + stringifyNumber(endDate.get(Calendar.HOUR_OF_DAY)) + ":" + stringifyNumber(endDate.get(Calendar.MINUTE));
        missionTime.setText(time);

        Long timeLeftMilliseconds = endDate.getTimeInMillis() - now.getTimeInMillis();
        if (timeLeftMilliseconds >= 6.048e+8) {
            timeLeft.setText(project.helpify.helpifyapp.R.string.timeGrtWeek);
        } else if (timeLeftMilliseconds >= 8.64e+7) {
            timeLeft.setText(project.helpify.helpifyapp.R.string.timeGrtDay);
        } else if (timeLeftMilliseconds < 0) {
            timeLeft.setText(project.helpify.helpifyapp.R.string.timeOver);
        } else {
            Long timeLeftHours = timeLeftMilliseconds / (60 * 60 * 1000) % 24;
            Long timeLeftMinutes = timeLeftMilliseconds / (60 * 1000) % 60;

            timeLeft.setText(stringifyNumber(timeLeftHours) + ":" + stringifyNumber(timeLeftMinutes));
        }
    }

    private void showDrawer() {
        if (!drawerUp) {

            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
            hiddenPanel.setVisibility(View.VISIBLE);
            drawerUp = true;
            checkAcceptance();
        }
    }

    private void hideDrawer() {
        if (drawerUp) {
            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
            hiddenPanel.setVisibility(View.INVISIBLE);
            drawerUp = false;
        }
    }

    /* -------------------------/DRAWER------------------------------------------------------------*/
    private String stringifyNumber(Integer number) {
        if (number < 10) {
            return "0" + number.toString();
        }
        return number.toString();
    }

    private String stringifyNumber(Long number) {
        if (number < 10) {
            return "0" + number.toString();
        }
        return number.toString();
    }


    private void checkAcceptance() {
        final Button accept_button = (Button) findViewById(R.id.acceptButton);
        final TextView uName = (TextView) findViewById(R.id.uName);

        mDatabase.child("quests").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String offering_user = uName.getText().toString();
                    String email = (String) snapshot.child("email").getValue();
                    String key = snapshot.getKey();

                    if (email.equals(offering_user) && drawerUp) {
                        mDatabase.child("quests").child(key).child("accepted_by").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                                    String data = datasnapshot.getKey();
                                    String current_user = firebaseAuth.getCurrentUser().getUid();
                                    if (data.equals(current_user)) {
                                        accept_button.setVisibility(View.INVISIBLE);
                                    } else {
                                        accept_button.setVisibility(View.VISIBLE);
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
    }


    private void questAfterUserTimestamp(final User user, final Quest quest) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(user.getTimestampLong());

        String month;
        String day;
        String hour;
        String minutes;

        if (cal.get(Calendar.MINUTE) < 10) {
            minutes = "0" + cal.get(Calendar.MINUTE);

        } else {
            minutes = "" + cal.get(Calendar.MINUTE);
        }

        if ((cal.get(Calendar.HOUR_OF_DAY) + 3) < 10) {
            hour = "0" + (cal.get(Calendar.HOUR_OF_DAY) + 3);

        } else {
            hour = "" + (cal.get(Calendar.HOUR_OF_DAY) + 3);
        }

        if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + cal.get(Calendar.DAY_OF_MONTH);

        } else {
            day = "" + cal.get(Calendar.DAY_OF_MONTH);
        }

        if ((cal.get(Calendar.MONTH) + 1) < 10) {
            month = "0" + (cal.get(Calendar.MONTH) + 1);

        } else {
            month = "" + (cal.get(Calendar.MONTH) + 1);
        }

        String userTimestampToDate = day +
                "/" + month +
                "/" + cal.get(Calendar.YEAR) +
                " " + hour +
                ":" + minutes;

        if (user.email.equals(quest.email)) {

            if (!quest.endDate.equals("NULL")) {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                try {
                    Date userDate = format.parse(userTimestampToDate);
                    Date questDate = format.parse(quest.endDate);
                    System.out.println(questDate);
                    System.out.println(userDate);
                    if (questDate.after(userDate)) {

                        addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);

                    } else if (questDate.equals(userDate)) {
                        if (Integer.parseInt(quest.endDate.substring(11, 13)) > Integer.parseInt(userTimestampToDate.substring(11, 13))) {

                            setMessage(true, userDate.toString() + "\n" + questDate.toString());
                        } else if (Integer.parseInt(quest.endDate.substring(11, 13)) == Integer.parseInt(userTimestampToDate.substring(11, 13)) &&
                                Integer.parseInt(quest.endDate.substring(14, 16)) >= Integer.parseInt(userTimestampToDate.substring(14, 16))) {

                            addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);
                        }
                    } else {
                        if (user.isOnline) {
                            addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void generateMap() {
        /*
        Check Android GPS permissions
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat
                        .requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);


            }
        }
        // Clear map for regeneration of circles
        mMap.clear();

        Location mLastLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            LatLng lastKnownLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            lastUserLocation = lastKnownLocation;

            //  save data to database
            String userId = firebaseAuth.getCurrentUser().getUid();
            String userEmail = firebaseAuth.getCurrentUser().getEmail();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            getUserIsHidden();
            if (isHidden != null && isHidden == false) {
                mDatabase.child("users").child(userId).setValue(new User(userEmail, mLastLocation.getLatitude(), mLastLocation.getLongitude(), true, isHidden));
                mDatabase.child("users").child(userId).onDisconnect().setValue(new User(userEmail, mLastLocation.getLatitude(), mLastLocation.getLongitude(), false, isHidden));
                addMarker(lastKnownLocation, 10, Color.GREEN, "USER");
            }
            if (!startingCameraPosition) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 14.0f));
                startingCameraPosition = true;
            }
        } else {
            setMessage(true, "Your location could not be accessed.");
        }

        //OTHER LOCATION MARKERS
        generateUserMarkers();

    }

    private void setMessage(boolean visibility) {
        final TextView message = (TextView) findViewById(R.id.message);
        if (visibility) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
        }
    }

    private void setMessage(boolean visibility, String text) {
        final TextView message = (TextView) findViewById(R.id.message);
        message.setText(text);
        if (visibility) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
        }
    }


    private void addMarker(LatLng location, Integer size, Integer color, String tag) {
        try {
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(location)
                    .radius(10)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(color));

            circle.setTag(tag);
            circle.setClickable(true);
            circle.setZIndex(1000 - size);

            mMap.addCircle(new CircleOptions()
                    .center(location)
                    .radius(20)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(1.5f));

            circle.setTag(tag);
            circle.setClickable(true);
            circle.setZIndex(1000 - size);

        } catch (java.lang.NullPointerException e) {
            setMessage(true, "Error M" + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }


    @Override
    public void onCameraMove() {
        if (drawerUp) {
            setMessage(true, "drawerUp");
            Animation bottomDown = AnimationUtils.loadAnimation(MapsActivity.this.getBaseContext(),
                    R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
            drawerUp = false;
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));
    }
}


//https://stackoverflow.com/questions/3438276/how-to-change-the-text-on-the-action-bar/3438352#3438352

