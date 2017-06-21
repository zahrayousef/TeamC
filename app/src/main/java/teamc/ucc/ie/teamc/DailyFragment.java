package teamc.ucc.ie.teamc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.User;


public class DailyFragment extends Fragment implements CalendarPickerController{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ProgressDialog dialog;
    private Serializable user;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DailyFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DailyFragment newInstance(int columnCount, User user) {
        DailyFragment fragment = new DailyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            user = getArguments().getSerializable("user");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_list, container, false);


        final AgendaCalendarView mAgendaCalendarView = (AgendaCalendarView) view.findViewById(R.id.agenda_calendar_view);

        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        final Calendar minDate = Calendar.getInstance();
        final Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);



        dialog = new ProgressDialog(getContext());


        if (dialog != null) dialog.show();
        User.getService().getEvent("").enqueue(new Callback<List<Event>>() {

            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                List<Event> events = response.body();

                    List<CalendarEvent> eventList = new ArrayList<>();
                    mockList(eventList, events);
                    mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), DailyFragment.this);


                    dialog.hide();

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                dialog.hide();

            }
        });


        return view;
    }








    private void mockList(List<CalendarEvent> eventList, List<Event> events) {

        for(Event event: events){
            Calendar startTime1 = Calendar.getInstance();
            startTime1.setTime(event.getStart());
            Calendar endTime1 = Calendar.getInstance();
            endTime1.setTime(event.getEnd());
            BaseCalendarEvent event1 = new BaseCalendarEvent(event.getTitle(),event.getDescription(), event.getLocation(),
                    ContextCompat.getColor(getContext(), R.color.orange_dark), startTime1, endTime1, true);
            eventList.add(event.getBaseCalander());
        }



    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Intent intent = new Intent(getContext(), EventViewActivity.class);
        if (event instanceof Event) {
            intent.putExtra("title", event.getTitle());
            intent.putExtra("desc", ((Event) event).getDescription());
            intent.putExtra("ids", ((Event) event).getIds());
            intent.putExtra("user", user);
            intent.putExtra("start", ((Event) event).getStart().getTime());
            intent.putExtra("end", ((Event) event).getEnd().getTime());
            intent.putExtra("type", ((Event)event).getType());
        };


        getActivity().startActivity(intent);

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

}
