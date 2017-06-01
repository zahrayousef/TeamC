package teamc.ucc.ie.teamc;

import org.junit.Test;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import teamc.ucc.ie.teamc.model.User;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-teamc-a1132.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        BackendService service = retrofit.create(BackendService.class);

        //User user = new User("dfgokwasfkl", "mujtaba.boori@gmail.com", true, "Mujtaba");

        //service.addUser("", user).execute();

        User reps = service.getUser("eyJhbGciOiJSUzI1NiIsImtpZCI6IjJiZGI2MzNkMjY1NmMyMTc5NGEwOTAxYThjY2RmODc3NzhhMmVmYjIifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vdGVhbWMtYTExMzIiLCJuYW1lIjoiWmFocmEiLCJhdWQiOiJ0ZWFtYy1hMTEzMiIsImF1dGhfdGltZSI6MTQ5NjI1NzA4NCwidXNlcl9pZCI6Imw1dUFra0twbzRRSW9BSVNMVU12cEV6T0haTjIiLCJzdWIiOiJsNXVBa2tLcG80UUlvQUlTTFVNdnBFek9IWk4yIiwiaWF0IjoxNDk2MjYyOTk4LCJleHAiOjE0OTYyNjY1OTgsImVtYWlsIjoiemF5b3VzZWYyMDE0QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTE3NDA0NTUwNTYwNTIwODc0ODU3Il0sImVtYWlsIjpbInpheW91c2VmMjAxNEBnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJnb29nbGUuY29tIn19.c_2OOopj7aErblM03Q-U8nVIVvHQFl5KkjzDirADZmGYoJsbxU4vqNLfPetds_50-Ym22COgJzYoXJ5i3lpNVS1aE0ES9NVGQwW4eLSZvGdb9z-I9MT9TbuhtB8F0XxXOsm-76p3-CYqkCBviQcaIRmF41MXiewno_WAP8-yBC9nJURxK4g9GnatNRtT-QCr92Hlfi5FERWxq-vILpN6wCU0NQQXLVbm4t_9d61k_ZMtC0Fs2CTgAdZeeNPGgwwxZDiBwAd5yNxLZXpme-4PKn0cCEsGz_YnNkXHlUoPtQVptYLcyrunX1xGsM4DK68jX2fEcSQ_wr56IK10dnkcnQ").execute().body();

       System.out.print(reps);

    }
}