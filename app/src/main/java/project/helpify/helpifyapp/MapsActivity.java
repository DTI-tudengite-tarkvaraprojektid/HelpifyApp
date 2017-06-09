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
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    private LatLng lastUserLocation;
    private Timer autoUpdate;
    private Boolean startingCameraPosition = false;
    int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x00111;


    private static final int MARKER_ICON_HEIGHT = 100;
    private static final int MARKER_ICON_WIDTH = 100;

    // FIREBASE

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private DataSnapshot dataSnapshot;
    private DataSnapshot questSnapshot;

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

    private void generateUserMarkers(){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if(user.isOnline){
                                if(hasUserTasks(user)){
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email);
                                } else {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private Boolean hasUserTasks(final User user){
        final Boolean[] flag = {false};
        FirebaseDatabase.getInstance().getReference().child("quests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Quest quest = dataSnapshot.getValue(Quest.class);
                        setMessage(true, quest.email + "\n" + user.email + "\n" + user.email.equals(quest.email));

                        if(user.email.equals(quest.email) && !quest.quest.equals("NULL")){
                                flag[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return flag[0];
    }


    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume(){
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
            public void onCircleClick(Circle circle) {
                Intent i = new Intent(MapsActivity.this, MapsUserClickActivity.class);
                String username = circle.getTag().toString();
                i.putExtra("username", username);
                startActivity(i);
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
        if(requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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




    private void generateMap(){
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
        }
        else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&  ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                
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

        if(mLastLocation != null){
            LatLng lastKnownLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                lastUserLocation = lastKnownLocation;

            // working here, save data to database
          String userId =  firebaseAuth.getCurrentUser().getUid();
            String userEmail =  firebaseAuth.getCurrentUser().getEmail();
          /*  Map<String,Object> checkoutData=new HashMap<>();
            checkoutData.put("time",ServerValue.TIMESTAMP);*/

            mDatabase = FirebaseDatabase.getInstance().getReference();

            Boolean isOnline = true;
            User user = new User(userEmail,mLastLocation.getLatitude(),mLastLocation.getLongitude(), isOnline);
            mDatabase.child("users").child(userId).setValue(user);
            isOnline = false;
            user = new User(userEmail,mLastLocation.getLatitude(),mLastLocation.getLongitude(), isOnline);
            mDatabase.child("users").child(userId).onDisconnect().setValue(user);

            //USER LOCATION MARKER
            addMarker(lastKnownLocation, 10, Color.GREEN, "USER");
            if(!startingCameraPosition){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 14.0f));
                startingCameraPosition = true;
            }
        } else {
            setMessage(true, "Your location could not be accessed.");
        }

        //OTHER LOCATION MARKERS
        generateUserMarkers();

    }


    private void setMessage(boolean visibility){
        final TextView message = (TextView) findViewById(R.id.message);
        if(visibility){
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);

        }
    }

    private void setMessage(boolean visibility, String text){
        final TextView message = (TextView) findViewById(R.id.message);
        message.setText(text);

        if(visibility){
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
        }
    }


    private void addMarker(LatLng location, Integer size, Integer color, String tag){
        try {
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(location)
                    .radius(10)
                    .strokeColor(color)
                    .fillColor(color));
            circle.setTag(tag);
            circle.setClickable(true);
            circle.setZIndex(1000-size);
        } catch (java.lang.NullPointerException e){

            setMessage(true, "Error M" + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }
}


//https://stackoverflow.com/questions/3438276/how-to-change-the-text-on-the-action-bar/3438352#3438352