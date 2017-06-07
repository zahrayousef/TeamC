package teamc.ucc.ie.teamc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.dummy.DummyContent;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.Rpe;
import teamc.ucc.ie.teamc.model.User;

public class EventViewActivity extends AppCompatActivity  implements AttendeeFragment.OnListFragmentInteractionListener{

    private TextView description;
    private String id;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        id = getIntent().getStringExtra("ids");



        user = (User) getIntent().getSerializableExtra("user");

        description = (TextView) findViewById(R.id.text_description);

        description.setText(getIntent().getStringExtra("desc"));

        findViewById(R.id.btn_rpe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(EventViewActivity.this);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EventViewActivity.this, android.R.layout.select_dialog_singlechoice, getResources().getStringArray(R.array.rpe_array));


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

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

                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        String token = task.getResult().getToken();
                                        User.getService().addRpe(token,new Rpe(id, pos + 1)).enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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

                                //dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.setTitle("Select One Name:-");

                builderSingle.show();




            }
        });

        findViewById(R.id.btn_attend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventViewActivity.this, "attend", Toast.LENGTH_SHORT).show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {


                        User.getService().addAttendee(task.getResult().getToken(),id).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Toast.makeText(EventViewActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                Toast.makeText(EventViewActivity.this, "Falied", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });



        if(user.isAdmin()) {
            findViewById(R.id.player_contl).setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AttendeeFragment.newInstance(0, id)).commit();

        }
    }


    @Override
    public void onListFragmentInteraction(User item) {

    }
}
