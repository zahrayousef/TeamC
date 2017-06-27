package teamc.ucc.ie.teamc;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * initialize code when the app is open
 * */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // require for JodaTime
        JodaTimeAndroid.init(this);
        //subscribeToTopic in firebase messaging so we can send push notification
        FirebaseMessaging.getInstance().subscribeToTopic("player");
    }
}