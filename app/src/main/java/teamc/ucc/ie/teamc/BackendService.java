package teamc.ucc.ie.teamc;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import teamc.ucc.ie.teamc.model.Event;
import teamc.ucc.ie.teamc.model.User;

/**
 * Created by zahra on 31/05/2017.
 */

public interface BackendService {

    @POST("addUser")
    Call<ResponseBody> addUser(@Header("token") String token, @Body User user);

    @GET("getUser")
    Call<User> getUser(@Header("token") String token);

    @POST("addEvent")
    Call<ResponseBody> addEvent(@Header("token") String token, @Body Event event);

    @GET("getEvent")
    Call<List<Event>> getEvent(@Header("token") String token);
}
