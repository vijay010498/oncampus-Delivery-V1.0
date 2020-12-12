package com.svijayr007.oncampusdelivery.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.model.DeliveryAgentUserModel;
import com.svijayr007.oncampusdelivery.model.TokenModel;

import java.util.HashMap;
import java.util.Map;

public class Common {


    //Database URLS
    public static final String serverValues = "https://eatitv2-75508-servervalues.firebaseio.com/";
    public static final String allTokensDB  = "https://eatitv2-75508-alltokens.firebaseio.com/";
    public static final  String partnerDb = "https://eatitv2-75508-partnerdb.firebaseio.com/";
    public static final String deliveryAgentsDB = "https://eatitv2-75508-deliveryagents.firebaseio.com/";
    public static final String deliveryOrdersDB = "https://eatitv2-75508-deliveryorders.firebaseio.com/";
    public static final String ordersDB = "https://eatitv2-75508-orders.firebaseio.com/";
    public static final String restaurantDB = "https://eatitv2-75508-restaurants.firebaseio.com/";

    //URLS
    public static final String onCampusPrivacy = "https://oncampus.in/privacy.html";
    public  static  final String onCampusWebsite = "https://oncampus.in/";

    //REFS
    public static final String DELIVERY_AGENT_REF = "deliveryAgents";
    public static final String TOKEN_REF = "Tokens" ;
    public static final String RESTAURANT_REF = "Restaurant";
    public static final String DELIVERY_ORDER_REF = "deliveryOrders";
    public static final String ORDER_REF = "Orders";
    public static DeliveryAgentUserModel currentDeliveryAgentUser;

    public static void updateLastVisitedTime(Context context) {
        if(currentDeliveryAgentUser != null){
            Map<String, Object> updateTime = new HashMap<>();
            updateTime.put("lastVisited",System.currentTimeMillis());
            FirebaseDatabase.getInstance(Common.deliveryAgentsDB)
                    .getReference(Common.DELIVERY_AGENT_REF)
                    .child(currentDeliveryAgentUser.getAgentId())
                    .updateChildren(updateTime)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Visited Time"+e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i("VISITED TIME","Last visited updated");
                    }

                }
            });
        }
    }

    public static void showNotification(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;

        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,pendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "onCampus";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //greater than oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "onCampus",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("onCampus");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[] {0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.oncampus_logo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.oncampus_logo));
        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }
    /************************************************************************/



    /************************************************************************/

    //checks for internet connectivity
    public static  boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }
        return false;
    }
    /************************************************************************/

    //custom browser
    public static void openCustomBrowser(Context context, String url){
        try{
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(context, Uri.parse(url));
            builder.setToolbarColor(context.getResources().getColor(R.color.pageColor2));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void createToken(Context context, String token) {
        //Create token for the very first time -- Sign up Activity
        if(Common.currentDeliveryAgentUser != null){
            FirebaseDatabase.getInstance(Common.allTokensDB)
                    .getReference(Common.TOKEN_REF)
                    .child(currentDeliveryAgentUser.getAgentId())
                    .setValue(new TokenModel(currentDeliveryAgentUser.getPhone(),token,currentDeliveryAgentUser.getAgentId()))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Create Token"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i("Token","Token Created");
                    }
                }
            });
        }
    }

    public static void updateToken(Context context, String token) {
        if(Common.currentDeliveryAgentUser != null)
        {
            Map<String, Object> updateToken = new HashMap<>();
            updateToken.put("token",token);
            FirebaseDatabase.getInstance(Common.allTokensDB)
                    .getReference(Common.TOKEN_REF)
                    .child(currentDeliveryAgentUser.getAgentId())
                    .updateChildren(updateToken)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Update Token "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i("TOKEN","Token Updated");
                    }

                }
            });

        }
    }
    /************************************************************************/


    /************************************************************************/

    public static String getDateOfWeek(int i) {
        switch (i){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Unknown";

        }
    }
    /************************************************************************/
}
