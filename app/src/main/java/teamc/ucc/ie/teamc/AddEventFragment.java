package teamc.ucc.ie.teamc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.User;

import static android.R.attr.minDate;


/**
 * Responsible for handling the coach input when he wants to add a new event
 * we used BetterPicker library for date picking
 * https://github.com/code-troopers/android-betterpickers
 * we used Joda time for date & time manipulation
 * https://github.com/dlew/joda-time-android
 */
public class AddEventFragment extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String DATE_START = "dateStart";
    private static final String DATE_END = "dateEnd";

    private static final String TIME_START = "timeStart";
    private static final String TIME_END = "timeEnd";


    private View view;
    private EditText startDate;
    private EditText endDate;
    private EditText startTime;
    private EditText endTime;
    private DateTime startDateJoda;
    private DateTime endDateJoda;
    private DateTime startTimeJoda;
    private DateTime endTimeJoda;

    private ProgressDialog dialog;
    private Spinner event_spinner;

    public AddEventFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        // find views for user input
        startDate = (EditText) view.findViewById(R.id.input_start);
        startTime = (EditText) view.findViewById(R.id.input_start_time);
        endDate = (EditText) view.findViewById(R.id.input_end);
        endTime = (EditText) view.findViewById(R.id.input_end_time);
        event_spinner = ((Spinner)view.findViewById(R.id.event_spinner));

        //
        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                showStartDate();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDate();
            }
        });

        startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                showStartTime();

            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime();
            }
        });



        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                showEndDate();

            }
        });

        endDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEndDate();
            }
            });



        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                showEndTime();
            }
        });

        endTime.setOnClickListener(new View .OnClickListener(){

            @Override
            public void onClick(View v) {
                showEndTime();
            }
        });




        // Handling add event click
        view.findViewById(R.id.add_event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //prepare date and time combine them into one object for startDate and EndDate
                // create a new DateTime object with date and time
                DateTime start = new DateTime(startDateJoda);
                start =  start.plusHours(startTimeJoda.getHourOfDay());
                start = start.plusMinutes(startTimeJoda.getMinuteOfHour());

                DateTime end = new DateTime(endDateJoda);
                end =  end.plusHours(endTimeJoda.getHourOfDay());
                end = end.plusMinutes(startTimeJoda.getMinuteOfHour());

                // get user inputs fields
                String title = ((EditText) view.findViewById(R.id.input_title)).getText().toString();
                String desc = ((EditText) view.findViewById(R.id.input_description)).getText().toString();
                String location = ((EditText) view.findViewById(R.id.input_location)).getText().toString();


                // create event object from the use input
                Event event = new Event(
                        title,
                        desc,
                        location,R.color.theme_primary,
                        start.toCalendar(Locale.ENGLISH),
                        end.toCalendar(Locale.ENGLISH),
                        event_spinner.getSelectedItem().toString(),
                        false);

                // post event to the firebase backend
                dialog.show();
                User.getService().addEvent("", event).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.hide();

                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("The event has been added")
                                .setTitle("Event");

                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
// 3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();

                        dialog.show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dialog.hide();

                    }
                });
                    ;
            }
        });
        return view;
    }

    private void showStartDate(){
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(AddEventFragment.this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setDoneText("Yes")
                .setCancelText("No");

        cdp.show(getActivity().getSupportFragmentManager(), DATE_START);
    }

    private void showStartTime(){

        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(AddEventFragment.this)
                .setStartTime(10, 10)
                .setDoneText("Yes")
                .setCancelText("No");
        rtpd.show(getActivity().getSupportFragmentManager(), TIME_START);

    }

    private void showEndDate(){

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(AddEventFragment.this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setDoneText("Yes")
                .setCancelText("No");
        cdp.show(getActivity().getSupportFragmentManager(), DATE_END);
    }

    private void showEndTime(){

        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(AddEventFragment.this)
                .setStartTime(10, 10)
                .setDoneText("Yes")
                .setCancelText("No");
        rtpd.show(getActivity().getSupportFragmentManager(), TIME_END);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

        if (dialog.getTag().equals(DATE_START)) {
            startDate.setText(dayOfMonth + "-" + (monthOfYear+1) + "-" + year);
            startDateJoda = new DateTime(year, (monthOfYear+1), dayOfMonth, 0,0);

        } else if (dialog.getTag().equals(DATE_END)){
            endDate.setText(dayOfMonth + "-" + (monthOfYear+1) + "-" + year);
            endDateJoda = new DateTime(year, (monthOfYear+1), dayOfMonth, 0,0);
        }
    }

    // listener when user select data
    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {

        if (dialog.getTag().equals(TIME_START)){
            startTime.setText(hourOfDay + ":" + minute);
            startTimeJoda = new DateTime(0, 1, 1, hourOfDay,minute);


        } else if (dialog.getTag().equals(TIME_END)){
            endTime.setText(hourOfDay + ":" + minute);
            endTimeJoda = new DateTime(0, 1, 1, hourOfDay,minute);
        }
    }


}
