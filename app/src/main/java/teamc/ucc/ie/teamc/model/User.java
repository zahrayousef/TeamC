package teamc.ucc.ie.teamc.model;

import java.io.Serializable;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import teamc.ucc.ie.teamc.BackendService;



public class User implements Serializable{
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", displayName='" + displayName + '\'' +", rpe ='" + rpe + '\'' +
                '}';
    }

    public User(String uid, String email, boolean isAdmin, String displayName) {
        this.uid = uid;
        this.email = email;
        this.isAdmin = isAdmin;
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String uid;
    private String email;
    private boolean isAdmin;
    private String displayName;
    private int rpe;


    public static BackendService getService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-teamc-a1132.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService service = retrofit.create(BackendService.class);
        return service;
    }

    public int getRpe() {
        return rpe;
    }

    public void setRpe(int rpe) {
        this.rpe = rpe;
    }
}
