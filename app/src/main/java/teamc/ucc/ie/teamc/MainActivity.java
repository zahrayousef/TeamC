package teamc.ucc.ie.teamc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import teamc.ucc.ie.teamc.model.User;

import static android.support.design.widget.NavigationView.*;

/**
 * The main homepage activity (screen)
 * */
public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, AttendeeFragment.OnListFragmentInteractionListener, AddEventFragment.OnFragmentInteractionListener  {



    private View fragmentContainer;
    private Toolbar toolbar;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(getIntent().getIntExtra("menu",R.menu.activity_main_drawer));

        user = (User) getIntent().getSerializableExtra("user");
        navigationView.setCheckedItem(R.id.nav_daily);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DailyFragment.newInstance(0,user)).commit();


        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_user)).setText(getIntent().getStringExtra("displayName"));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email)).setText(getIntent().getStringExtra("email"));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_title)).setText(getIntent().getStringExtra("title"));
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)  {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DailyFragment.newInstance(0, user)).commit();

        } else if (id == R.id.nav_slideshow) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddEventFragment.newInstance("","")).commit();
        }

        else if (id == R.id.nav_logout) {

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        } else if (id == R.id.nav_analytics){
            startActivity(new Intent(this, GraphActivity.class));
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void changeTitle(String string) {
        toolbar.setTitle(string);
    }





    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(User item) {

    }
}
