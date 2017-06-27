package teamc.ucc.ie.teamc;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.Rpe;
import teamc.ucc.ie.teamc.model.User;

/**
 * This handle displaying event details
 * we use retrofit to get event details.
 * http://square.github.io/retrofit/
 *
 * */
public class EventViewActivity extends AppCompatActivity  implements AttendeeFragment.OnListFragmentInteractionListener{

    private TextView description;
    private String id;
    private User user;
    private String type;
    private int eventType = 0;
    private DateTime start;
    private DateTime end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //get data from past activity
        id = getIntent().getStringExtra("ids");
        type = getIntent().getStringExtra("type");
        user = (User) getIntent().getSerializableExtra("user");
        start =  new DateTime(getIntent().getLongExtra("start",0));
        end =  new DateTime(getIntent().getLongExtra("end",0));

// if type is training show the RPE Button if not hide the button
        if (type.equals("Training")){
            eventType = 1;

        } else {
            findViewById(R.id.btn_rpe).setVisibility(View.GONE);
        }




        description = (TextView) findViewById(R.id.text_description);
        TextView startDate = (TextView) findViewById(R.id.text_start_date);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd, MMMM, yyyy @ HH:mmaa");
        String dateTxt = fmt.print(start) + DateTimeFormat.forPattern(" 'till' HH:mmaa").print(end);

        //set the text to display event description and date
        startDate.setText(dateTxt);
        description.setText(getIntent().getStringExtra("desc"));


        // Handle RPE button click
        findViewById(R.id.btn_rpe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(EventViewActivity.this);

                // create adapter that hold RPE score
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EventViewActivity.this, android.R.layout.select_dialog_singlechoice, getResources().getStringArray(R.array.rpe_array));

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //set the adapter
                // https://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        final int pos = which;

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(EventViewActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {

                                final AlertDialog.Builder enterDurationDialog = new AlertDialog.Builder(EventViewActivity.this);
                                enterDurationDialog.setTitle("Duration");
                                enterDurationDialog.setMessage("Enter Duration");
                                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                final View view = inflater.inflate(R.layout.dialog_duration,null);

                                enterDurationDialog.setView(view);
                                enterDurationDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {

                                        //once user select the score and duration, we post the data to our backend
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        mAuth.getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                String token = task.getResult().getToken();

                                                TextView duration = (TextView) view.findViewById(R.id.duration_text);
                                                User.getService()
                                                        .addRpe(token,new Rpe(id, pos + 1, Integer.valueOf(duration.getText().toString())))
                                                        .enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Toast.makeText(EventViewActivity.this, call.request().body().toString(), Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                        dialog.dismiss();
                                                        Toast.makeText(EventViewActivity.this, call.request().body().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                                enterDurationDialog.show();


                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.setTitle("Select One Name:-");

                builderSingle.show();




            }
        });

        // Handle when user click attend and add it to the backend
        findViewById(R.id.btn_attend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {


                        User.getService().addAttendee(task.getResult().getToken(),id).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {


                            }
                        });
                    }
                });
            }
        });




//if the user is coach, hide attend and RPE score button and display attendance and REP score details
        if(user.isAdmin()) {
            findViewById(R.id.player_contl).setVisibility(View.GONE);
            // Get the ViewPager and set it's PagerAdapter so that it can display items
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                    EventViewActivity.this, id, eventType));

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

        }
    }


    @Override
    public void onListFragmentInteraction(User item) {

    }
//This adapter handle displaying list of attendance and REP score tabs
    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private Fragment[] fragments;
        private String tabTitles[];
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context, String eventId, int type) {
            super(fm);
            this.context = context;

            if (type == 0) {
                fragments = new Fragment[]{AttendeeFragment.newInstance(0, eventId, MyRpeRecyclerViewAdapter.TYPE_ATTEND)};
                tabTitles = new String[] { "Attendance"};
            } else if (type == 1) {
                fragments = new Fragment[]{AttendeeFragment.newInstance(0, eventId, MyRpeRecyclerViewAdapter.TYPE_ATTEND),
                        AttendeeFragment.newInstance(0, eventId, MyRpeRecyclerViewAdapter.TYPE_RPE)};
                tabTitles = new String[] { "Attendance", "RPE Score" };
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            //return PageFragment.newInstance(position + 1);
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
