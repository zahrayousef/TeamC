package teamc.ucc.ie.teamc;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        FirebaseMessaging.getInstance().subscribeToTopic("player");
    }
}