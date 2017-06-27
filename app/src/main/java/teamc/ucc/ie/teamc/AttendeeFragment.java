package teamc.ucc.ie.teamc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import teamc.ucc.ie.teamc.model.User;

/**
 * This display players attending and RPE score we get data from the firebase functions
 * https://developer.android.com/training/material/lists-cards.html
 */
public class AttendeeFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_EVENT_ID = "event-id";
    private static final String ARG_TYPE = "type";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String eventid;
    private OnListFragmentInteractionListener mListener;
    private View view;
    private RecyclerView recyclerView;
    private int type;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AttendeeFragment() {
    }



    /**
     * Create fragment instance with the following argument
     */
    public static AttendeeFragment newInstance(int columnCount, String eventid, int type) {
        AttendeeFragment fragment = new AttendeeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_EVENT_ID, eventid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            eventid = getArguments().getString(ARG_EVENT_ID);
            type = getArguments().getInt(ARG_TYPE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_rpe_list, container, false);

        // Set the adapter
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }



            // get loged in user
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                // if user is loged in get the data from the backend
                mAuth.getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        // if we are displaying attending, get the attending data otherwise get RPE score
                        if (type == MyRpeRecyclerViewAdapter.TYPE_ATTEND) {
                            // make request and once we recive the data we assign it to list adapter
                            User.getService().getAttendee(task.getResult().getToken(), eventid).enqueue(new Callback<List<User>>() {
                                @Override
                                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                    ((TextView) view.findViewById(R.id.text_num_players)).setText(String.valueOf(response.body().size()));
                                    recyclerView.setAdapter(new MyRpeRecyclerViewAdapter(response.body(), type, mListener));

                                }

                                @Override
                                public void onFailure(Call<List<User>> call, Throwable t) {

                                }
                            });
                        } else if (type == MyRpeRecyclerViewAdapter.TYPE_RPE){

                            User.getService().getRpe(task.getResult().getToken(), eventid).enqueue(new Callback<List<User>>() {
                                @Override
                                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                    ((TextView) view.findViewById(R.id.text_num_players)).setText(String.valueOf(response.body().size()));
                                    recyclerView.setAdapter(new MyRpeRecyclerViewAdapter(response.body(), type, mListener));
                                }

                                @Override
                                public void onFailure(Call<List<User>> call, Throwable t) {

                                    //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("app", t.getMessage());
                                }
                            });

                        }
                    }
                });
            }
        }
        return view;
    }

/**
 * Attach listiner so we can listen for user selection, we current not do anything when user click
 * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    // make sure to remove the listner to avoid exception
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(User item);
    }
}
