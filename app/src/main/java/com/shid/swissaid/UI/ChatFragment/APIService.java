package com.shid.swissaid.UI.ChatFragment;



import com.shid.swissaid.Notification.MyResponse;
import com.shid.swissaid.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAvDoH22s:APA91bFyz3LNWPZJ71mxiISMJfSmb7XBycPnX5vqlaFLaPMNhmD6FsgqI2NTFxk6hOGQTMuX1F5tBcw-7EMNDG1dhMwDGzKDGu2x2FIv_NNO5MtqoZNxHAt4jFhptToLKgIEhiYQz2ft"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
