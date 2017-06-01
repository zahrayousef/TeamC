package teamc.ucc.ie.teamc;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import teamc.ucc.ie.teamc.model.User;

/**
 * Created by zahra on 31/05/2017.
 */

public interface BackendService {

    @POST("addUser")
    Call<ResponseBody> addUser(@Header("token") String token, @Body User user);

    @GET("getUser")
    Call<User> getUser(@Header("token") String token);
}
