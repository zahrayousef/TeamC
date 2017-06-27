package teamc.ucc.ie.teamc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.Rpe;
import teamc.ucc.ie.teamc.model.User;

/**
 * This actvity responsible for displaying Graph
 * we used https://github.com/PhilJay/MPAndroidChart
 * */
public class GraphActivity extends AppCompatActivity {

    private LineChart chart;
    private Spinner playersSpinner;
    private ArrayAdapter<String> adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        chart = (LineChart) findViewById(R.id.chart);
        playersSpinner =(Spinner) findViewById(R.id.spinner);


        // retrieve all the players data
        User.getService().getPlayers("").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                // get the response
                users = response.body();
                // create an array with name of the player
                List<String> players = new ArrayList<String>();
                for(User user: users) {
                    players.add(user.getDisplayName());
                }

                // set the adapter that hold the players name
                adapter = new ArrayAdapter<String>(
                        GraphActivity.this, android.R.layout.simple_spinner_item, players);
                playersSpinner.setAdapter(adapter);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                // create a listener
                playersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (users != null) getUserStat(users.get(position).getUid());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });


    }

    private void getUserStat(String uid){

        //get all rpe scores for specific user
        User.getService().getStatUser("", uid).enqueue(new Callback<List<Rpe>>() {
            @Override
            public void onResponse(Call<List<Rpe>> call, Response<List<Rpe>> response) {


                List<Rpe> rpeList = response.body();
                if (rpeList != null && rpeList.size() > 0) {
                    // sort RPE score by date
                    Collections.sort(rpeList, new Comparator<Rpe>() {
                        public int compare(Rpe o1, Rpe o2) {
                            return o1.getEvent().getStart().compareTo(o2.getEvent().getStart());
                        }
                    });

                    // create points for the chart
                    List<Point> points = new ArrayList<Point>();
                    for (Rpe rpe : rpeList) {
                        //get date in milliseconds
                        long x = new DateTime(rpe.getEvent().getStart()).getMillis();
                        // calculate the training load
                        float y = rpe.getDuration() * rpe.getScore();
                        points.add(new Point(x, y));
                    }

                    // build graph from the points we have created
                    buildGraph(points);
                }


            }

            @Override
            public void onFailure(Call<List<Rpe>> call, Throwable t) {


            }
        });

    }

    /**
     * function that take list of points x is the date and y is the training load
     * **/
    protected void buildGraph(List<Point> points){

        // create List of entries for Chart view
        List<Entry> entries = new ArrayList<Entry>();

        for (Point data : points) {

            // turn your data into Entry objects
            entries.add(new Entry(data.x, data.y));
        }

        // create a line data set from the entry
        LineDataSet dataSet = new LineDataSet(entries, "Training Load"); // add entries to dataset

        // create lineData
        LineData lineData = new LineData(dataSet);
        //lineData.setValueTextSize(18);



        // configre the chart
        chart.getDescription().setEnabled(false);
        chart.setData(lineData);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // format the x axis to represent Date instead of long
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {


                long millis = TimeUnit.DAYS.toMillis((long) value);
                return mFormat.format(new Date((long) value));
            }
        });

        chart.invalidate();




    }


    class Point {
        float x;
        float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
