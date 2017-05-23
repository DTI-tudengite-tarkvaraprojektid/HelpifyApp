package project.helpify.helpifyapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.w3c.dom.Text;

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
    int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x00111;

    private static final int MARKER_ICON_HEIGHT = 100;
    private static final int MARKER_ICON_WIDTH = 100;


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

    //If user clicks a marker, do what?
    public boolean onMarkerClick(final Marker marker){
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateMap(){
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
        mMap.clear();
        Location mLastLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation != null){
            LatLng lastKnownLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    lastUserLocation = lastKnownLocation;


            //USER LOCATION MARKER
            addMarker(lastKnownLocation, "You!");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 14.0f));
        } else {
            setMessage(true, "Your location could not be accessed.");
        }

        //OTHER LOCATION MARKERS

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



    private void addMarker(LatLng location, String title){


        try{

            BitmapDrawable markerIcon = (BitmapDrawable) ResourcesCompat
                    .getDrawable(getResources(), R.drawable.circle_512, null);
            assert markerIcon != null;
            Bitmap markerIconBitmap = markerIcon
                    .getBitmap();
            Bitmap smallerIcon = Bitmap
                    .createScaledBitmap(markerIconBitmap, MARKER_ICON_WIDTH/10, MARKER_ICON_HEIGHT/10, false);


            LatLng nwCorner = new LatLng(
                    lastUserLocation.latitude - (MARKER_ICON_HEIGHT/75), //KORGUS
                    lastUserLocation.longitude - (MARKER_ICON_WIDTH/75) //PIKKUS
            );

            LatLng seCorner = new LatLng(
                    lastUserLocation.latitude + (MARKER_ICON_HEIGHT/75), //KORGUS
                    lastUserLocation.longitude + (MARKER_ICON_WIDTH/75) //PIKKUS

            );

            setMessage(true, "NW:" + nwCorner.toString() + "\nSE: " + seCorner.toString() + "\nUser:" + lastUserLocation.toString());
            LatLngBounds latLngBounds = new LatLngBounds(
                    nwCorner,
                    seCorner
            );

           mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(smallerIcon))
                .positionFromBounds(latLngBounds));

//            mMap
//                    .addMarker(new MarkerOptions()
//                            .icon(BitmapDescriptorFactory.fromBitmap(smallerIcon))
//                            .position(location)
//                            .title(title)
//                            .flat(false));
        } catch (java.lang.NullPointerException e){
            //Image not found, resetting to default pin image
            mMap
                    .addMarker(new MarkerOptions()
                            .position(location)
                            .title(title)
                            .flat(false));
            setMessage(true, "Some files could not be found. Please reinstall! 233");
        }
    }
}
