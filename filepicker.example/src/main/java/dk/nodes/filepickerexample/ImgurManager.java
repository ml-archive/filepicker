package dk.nodes.filepickerexample;

import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by joso on 16/11/2016.
 */

public class ImgurManager {

    ImgurService service;

    public ImgurManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ImgurService.class);
    }

    public void uploadImage(File file, final UploadCallback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);

        Call<ImageResponse> call = service.postImage(requestBody);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response.isSuccessful() && callback != null) {
                    callback.onUploaded(response.body());
                } else {
                    Log.e("UploadImage", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Log.e("UploadImage", t.toString());
            }
        });
    }


    public interface ImgurService {
        @POST("3/image")
        @Multipart
        @Headers("Authorization: Client-ID c006fb01daee987")
        Call<ImageResponse> postImage(@Part("image") RequestBody image);
    }

    public interface UploadCallback {
        void onUploaded(ImageResponse response);
    }

    public class Image {
        public String id;
        public String title;
        public String link;
    }

    public class ImageResponse {
        public Image data;
    }
}
