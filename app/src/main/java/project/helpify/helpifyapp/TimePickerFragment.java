package project.helpify.helpifyapp;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

/**
 * Created by Mariam on 14.06.2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

     TextView tv = (TextView) getActivity().findViewById(R.id.tv);

    //Display the user changed time on TextView
        tv.setText(hourOfDay+":"+minute);
}
}