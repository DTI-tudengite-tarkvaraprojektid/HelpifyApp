package project.helpify.helpifyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    private LatLng lastUserLocation;
    private Timer autoUpdate;
    private Boolean startingCameraPosition = false;
    private Boolean drawerUp = false;
    int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x00111;


    private static final int MARKER_ICON_HEIGHT = 100;
    private static final int MARKER_ICON_WIDTH = 100;

    // FIREBASE

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void generateUserMarkers() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            hasUserTasks(user);
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

                            if (user.isOnline) {


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
                                }
                            } else {

                                questAfterUserTimestamp(user, quest);
                                // break;
                            }

                        }

                    }

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
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generateMap();
                    }
                });
            }
        }, 0, 30000);
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
//                Intent i = new Intent(MapsActivity.this, MapsUserClickActivity.class);
//                String username = circle.getTag().toString();
//                i.putExtra("username", username);
//                startActivity(i);
                if (!circle.getTag().toString().equals("NULL") && !circle.getTag().toString().equals("USER")) {
                    FirebaseDatabase.getInstance().getReference().child("quests")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Quest quest = snapshot.getValue(Quest.class);
                                        if(circle.getTag().toString().equals(quest.email)){
                                            generateDrawer(quest.name, quest.email, quest.startDate, quest.endDate);
                                            showDrawer();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

/* -------------------------DRAWER------------------------------------------------------------*/
    public void generateDrawer(String name, String username, String start, String end) {
        TextView uName = (TextView) findViewById(R.id.uName);
        TextView missionTime = (TextView) findViewById(R.id.missionTime);
        TextView missionName = (TextView) findViewById(R.id.missionName);
        TextView timeLeft = (TextView) findViewById(R.id.timeLeft);

        uName.setText(username);
        missionName.setText(name);
        Calendar now = Calendar.getInstance();
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            startDate.setTime(dateFormat.parse(start));
            endDate.setTime(dateFormat.parse(end));
        } catch (ParseException e){
            e.printStackTrace();
        }

        String time = stringifyNumber(startDate.get(Calendar.HOUR_OF_DAY)) + ":" + stringifyNumber(startDate.get(Calendar.MINUTE)) +
                " - " + stringifyNumber(endDate.get(Calendar.HOUR_OF_DAY)) + ":" + stringifyNumber(endDate.get(Calendar.MINUTE));
        missionTime.setText(time);

        Long timeLeftMilliseconds = endDate.getTimeInMillis() - now.getTimeInMillis();
        if (timeLeftMilliseconds >= 6.048e+8){
            timeLeft.setText("More than a week");
        } else {
            Long  timeLeftHours = timeLeftMilliseconds / (60 * 60 * 1000) % 24;
            Long timeLeftMinutes = timeLeftMilliseconds / (60 * 1000) % 60;

            timeLeft.setText(stringifyNumber(timeLeftHours) + ":" + stringifyNumber(timeLeftMinutes));
        }

    }

    private void showDrawer(){
        if(!drawerUp){
//            Animation bottomUp = AnimationUtils.loadAnimation(MapsActivity.this.getBaseContext(),
//                    R.anim.bottom_up);
            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
//            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            drawerUp = true;
        }
    }

    private void hideDrawer(){
        if(drawerUp){
//            Animation bottomDown = AnimationUtils.loadAnimation(MapsActivity.this.getBaseContext(),
//                    R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
//
//            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
            drawerUp = false;
        }
    }
/* -------------------------/DRAWER------------------------------------------------------------*/
    private String stringifyNumber(Integer number){
        if (number<10){
            return "0" + number.toString();
        }
        return number.toString();
    }

    private String stringifyNumber(Long number){
        if (number<10){
            return "0" + number.toString();
        }
        return number.toString();
    }

    private void questAfterUserTimestamp(final User user, final Quest quest) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(user.getgetTimestampLong());

        String month = "";
        String day = "";
        String hour = "";
        String minutes = "";

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

                    if (questDate.after(userDate)) {

                        addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);

                    } else if (questDate.equals(userDate)) {
                        if (Integer.parseInt(quest.endDate.substring(11, 13)) > Integer.parseInt(userTimestampToDate.substring(11, 13))) {

                            setMessage(true, userDate.toString() + "\n" + questDate.toString());
                        } else if (Integer.parseInt(quest.endDate.substring(11, 13)) == Integer.parseInt(userTimestampToDate.substring(11, 13))) {
                            if (Integer.parseInt(quest.endDate.substring(14, 16)) >= Integer.parseInt(userTimestampToDate.substring(14, 16))) {
                                addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);
                            }
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

            // working here, save data to database
            String userId = firebaseAuth.getCurrentUser().getUid();
            String userEmail = firebaseAuth.getCurrentUser().getEmail();
          /*  Map<String,Object> checkoutData=new HashMap<>();
            checkoutData.put("time",ServerValue.TIMESTAMP);*/

            mDatabase = FirebaseDatabase.getInstance().getReference();

            Boolean isOnline = true;
            User user = new User(userEmail, mLastLocation.getLatitude(), mLastLocation.getLongitude(), isOnline);
            mDatabase.child("users").child(userId).setValue(user);
            isOnline = false;
            user = new User(userEmail, mLastLocation.getLatitude(), mLastLocation.getLongitude(), isOnline);
            mDatabase.child("users").child(userId).onDisconnect().setValue(user);

            //USER LOCATION MARKER
            addMarker(lastKnownLocation, 10, Color.GREEN, "USER");
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

            Circle circleOuter = mMap.addCircle(new CircleOptions()
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
        if (drawerUp){
            setMessage(true, "drawerUp");
            Animation bottomDown = AnimationUtils.loadAnimation(MapsActivity.this.getBaseContext(),
                    R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
            TextView uEmail = (TextView) findViewById(R.id.uEmail);
            uEmail.setText(null);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
            drawerUp = false;
        }
    }
}


//https://stackoverflow.com/questions/3438276/how-to-change-the-text-on-the-action-bar/3438352#3438352