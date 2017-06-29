package joroze.com.roomifer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by roseje57 on 4/18/2017.
 */

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog.OnDateSetListener listener;


    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener) {
        super();
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Create and return a new instance of TimePickerDialog
        return new DatePickerDialog(getActivity(),listener, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
