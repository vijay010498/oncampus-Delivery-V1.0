package com.svijayr007.oncampusdelivery.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.svijayr007.oncampusdelivery.common.Common;

import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null) {
            Common.showNotification(this, new Random().nextInt(), remoteMessage.getNotification().getTitle()
                    , remoteMessage.getNotification().getBody(), null);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if(Common.currentDeliveryAgentUser != null)
            if(Common.currentDeliveryAgentUser.getAgentId() != null)
                Common.updateToken(this,token);
    }

}
