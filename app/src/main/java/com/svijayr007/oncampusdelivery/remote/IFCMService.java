package com.svijayr007.oncampusdelivery.remote;

import com.svijayr007.oncampusdelivery.model.FCMResponse;
import com.svijayr007.oncampusdelivery.model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA95Jb2LE:APA91bEwh-_I-BrfnPeHjGPN4llv9Plmm5TWwZ5wNynKwjL66jkAtkqIAF7_M8nOuFkR_uCUi-9kYgeMIxu4it98Pc3jsFlA58m-lEd_g2YKa-qOT342FgNLVMd9YAmxo0CqBLG1Nz_N"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
