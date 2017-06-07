package teamc.ucc.ie.teamc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teamc.ucc.ie.teamc.model.User;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "mainactivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RC_SIGN_IN = 123;
    private Intent signIn;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    dialog.show();
                    user.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            Log.d(TAG, "Token:" + task.getResult().getToken());
                            login(task.getResult().getToken(), user);



                        }
                    });
                } else {
                    // User is signed out

                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            signIn,
                            RC_SIGN_IN);

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        signIn = AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false)
                .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .setTheme(R.style.GreenTheme).setLogo(R.drawable.logo).build();

        // retrive User
        //if Empty
        // Register The user
        // If not Loged the user
    }

    private void login(final String token, final FirebaseUser userFire){
        BackendService service = User.getService();
        dialog.show();
        service.getUser(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dialog.hide();
                User user = response.body();
                if (user.getUid() == null){
                    showSelection(token, userFire);

                } else {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("displayName", user.getDisplayName());
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("user", user);
                    if (user.isAdmin()){
                        intent.putExtra("menu", R.menu.activity_main_coach_drawer);
                        startActivity(intent);
                        finish();
                    }else {

                        intent.putExtra("menu", R.menu.activity_main_drawer);
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Log.d(TAG, "Faile" + t.getMessage());
            }
        });
    }

    /**
     Let the user select if he is a coauch or player
     **/
    private void showSelection(final String token, final FirebaseUser user) {

        findViewById(R.id.selection_layout).setVisibility(View.VISIBLE);

        final BackendService service = User.getService();


        findViewById(R.id.player_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final User userModel = new User(user.getUid(), user.getEmail(), false, user.getDisplayName());
                dialog.show();
                service.addUser(token, userModel).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.hide();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("menu", R.menu.activity_main_drawer);
                        intent.putExtra("displayName", userModel.getDisplayName());
                        intent.putExtra("email", userModel.getEmail());
                        intent.putExtra("user", userModel);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        dialog.hide();
                    }
                });

            }
        });

        findViewById(R.id.coach_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User userModel = new User(user.getUid(), user.getEmail(), true, user.getDisplayName());
                dialog.show();
                service.addUser(token, userModel).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.hide();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("menu", R.menu.activity_main_coach_drawer);
                        intent.putExtra("displayName", userModel.getDisplayName());
                        intent.putExtra("email", userModel.getEmail());
                        intent.putExtra("user", userModel);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        dialog.hide();
                    }
                });
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                //startActivity(SignedInActivity.createIntent(this, response));

                final FirebaseUser user = mAuth.getCurrentUser();


                if(user != null) {
                    user.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            login(task.getResult().getToken(), user);
                        }
                    });
                }



                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            //showSnackbar(R.string.unknown_sign_in_response);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
