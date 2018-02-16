// Wrapper for Retrofit image uploading
// @author: Christopher Besser
package com.archaeology.services;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public class RetrofitWrapper
{
    public interface FileUploadService
    {
        /**
         * Upload an image to the server
         * @param description - upload type
         * @param file - file to upload
         * @return Returns the call
         */
        @Multipart
        @POST("upload")
        Call<ResponseBody> upload(@Part("description") RequestBody description,
                                  @Part MultipartBody.Part file);
    }

    /**
     * Upload a file to the server
     * @param URL - server URL to post to
     * @param fileURI - file to upload
     * @param easting - artifact easting
     * @param northing - artifact northing
     * @param context - artifact context
     * @param sample - artifact sample
     */
    public static void uploadFile(String URL, Uri fileURI, String easting, String northing,
                                  String context, String sample)
    {
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
        File file = new File(fileURI.getPath());
        String type = fileURI.getPath().substring(fileURI.getPath().lastIndexOf('.') + 1);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + type), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(),
                requestFile);
        FormBody.Builder formBuilder = new FormBody.Builder().add("easting", easting)
                .add("northing", northing).add("context", context)
                .add("sample", sample).add("file_name", file.getName());
        RequestBody formBody = formBuilder.build();
        Call<ResponseBody> call = service.upload(formBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Response received
             * @param call - server request call
             * @param response - server response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.v("Upload", "success");
            }

            /**
             * Upload failed
             * @param call - failed call
             * @param t - error
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}