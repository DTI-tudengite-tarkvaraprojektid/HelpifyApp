package project.helpify.helpifyapp;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by User on 20-Jun-17.
 */

class MapsFunctions {
    private FirebaseAuth firebaseAuth;

    void hasUserTasks(final User user, final GoogleMap mMap) {

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
                                if (user.email.equals(quest.email) && !questAfterUserTimestamp(user, quest)) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email, mMap);
                                } else if (!quests.contains(user.email)) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.BLUE, user.email, mMap);
                                } else if (user.email.equals(quest.email) && !quest.quest.equals("NULL") && questAfterUserTimestamp(user, quest)) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email, mMap);
                                }
                            } else if (!user.isOnline && !user.isHidden && user.email.equals(quest.email)) {
                                if (questAfterUserTimestamp(user, quest)) {
                                    addMarker(new LatLng(user.latitude, user.longitude), 400, Color.RED, user.email, mMap);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private boolean questAfterUserTimestamp(final User user, final Quest quest) {

        if (user.email.equals(quest.email)) {

            if (!quest.endDate.equals("NULL")) {

                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Calendar userDate = Calendar.getInstance();
                    Calendar questDate = new GregorianCalendar();
                    questDate.setTime(format.parse(quest.endDate));
                    System.out.println(questDate);
                    System.out.println(userDate);

                    Long left = questDate.getTimeInMillis() - userDate.getTimeInMillis();

                    if (left > 0 || left == 0) {
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    void addMarker(LatLng location, Integer size, Integer color, String tag, GoogleMap mMap) {
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
            //  setMessage(true, "Error M" + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
    }

    String stringifyNumber(Integer number) {
        if (number < 10) {
            return "0" + number.toString();
        }
        return number.toString();
    }

    String stringifyNumber(Long number) {
        if (number < 10) {
            return "0" + number.toString();
        }
        return number.toString();
    }
}
