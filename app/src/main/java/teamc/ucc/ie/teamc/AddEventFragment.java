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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEventFragment extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String DATE_START = "dateStart";
    private static final String DATE_END = "dateEnd";

    private static final String TIME_START = "timeStart";
    private static final String TIME_END = "timeEnd";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
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

    public AddEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance(String param1, String param2) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        startDate = (EditText) view.findViewById(R.id.input_start);
        startTime = (EditText) view.findViewById(R.id.input_start_time);
        endDate = (EditText) view.findViewById(R.id.input_end);
        endTime = (EditText) view.findViewById(R.id.input_end_time);

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(AddEventFragment.this)
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDoneText("Yes")
                        .setCancelText("No");

                cdp.show(getActivity().getSupportFragmentManager(), DATE_START);
            }
        });

        startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(AddEventFragment.this)
                        .setStartTime(10, 10)
                        .setDoneText("Yes")
                        .setCancelText("No");
                rtpd.show(getActivity().getSupportFragmentManager(), TIME_START);
            }
        });



        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(AddEventFragment.this)
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDoneText("Yes")
                        .setCancelText("No");
                cdp.show(getActivity().getSupportFragmentManager(), DATE_END);
            }
        });

        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(AddEventFragment.this)
                        .setStartTime(10, 10)
                        .setDoneText("Yes")
                        .setCancelText("No");
                rtpd.show(getActivity().getSupportFragmentManager(), TIME_END);
            }
        });


        view.findViewById(R.id.add_event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateTime start = new DateTime(startDateJoda);

                //start = start.plus(startTimeJoda.getMillis());

                start =  start.plusHours(startTimeJoda.getHourOfDay());
                start = start.plusMinutes(startTimeJoda.getMinuteOfHour());

                DateTime end = new DateTime(endDateJoda);

                end =  end.plusHours(endTimeJoda.getHourOfDay());
                end = end.plusMinutes(startTimeJoda.getMinuteOfHour());

                String title = ((EditText) view.findViewById(R.id.input_title)).getText().toString();
                String desc = ((EditText) view.findViewById(R.id.input_description)).getText().toString();
                String location = ((EditText) view.findViewById(R.id.input_location)).getText().toString();


                java.util.Calendar startTime1 = java.util.Calendar.getInstance();
                startTime1.setTime(start.toDate());
                Event event = new Event(title, desc,location,R.color.theme_primary, start.toCalendar(Locale.ENGLISH), end.toCalendar(Locale.ENGLISH),false);

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
