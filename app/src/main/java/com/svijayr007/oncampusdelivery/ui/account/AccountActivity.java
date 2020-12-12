package com.svijayr007.oncampusdelivery.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.svijayr007.oncampusdelivery.MainActivity;
import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.ui.no_internet.NoInternetActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import spencerstudios.com.bungeelib.Bungee;

public class AccountActivity extends AppCompatActivity {
    @BindView(R.id.user_name)
    TextView user_name;
    @BindView(R.id.user_phone)
    TextView user_phone;
    @BindView(R.id.user_email)
    TextView user_email;
    @BindView(R.id.text_current_restaurant)
    TextView text_current_restaurant;

    @BindView(R.id.mailUsTV)
    TextView mailUsTV;
    @BindView(R.id.websiteTV)
    TextView websiteTV;
    @BindView(R.id.privacyTV)
    TextView privacyTV;
    @BindView(R.id.faqTV)
    TextView faqTV;
    @BindView(R.id.logoutTV)
    TextView logoutTV;

    @BindView(R.id.backIV)
    ImageView backIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        initUi();
        setListener();
    }

    private void setListener() {
        mailUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:contact@oncampus.in"));
                try{
                    startActivity(emailIntent);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Privacy
        privacyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.isInternetAvailable(getApplicationContext())){
                    Common.openCustomBrowser(getApplicationContext(), Common.onCampusPrivacy);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                    startActivity(intent);
                    Bungee.fade(AccountActivity.this);
                }

            }
        });


        //website
        websiteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isInternetAvailable(getApplicationContext())){
                    Common.openCustomBrowser(getApplicationContext(), Common.onCampusWebsite);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                    startActivity(intent);
                    Bungee.fade(AccountActivity.this);
                }
            }
        });
        //Faq
        faqTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isInternetAvailable(getApplicationContext())){
                    Common.openCustomBrowser(getApplicationContext(), Common.onCampusWebsite);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                    startActivity(intent);
                    Bungee.fade(AccountActivity.this);
                }
            }
        });

        //Logout
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentDeliveryAgentUser = null;
                FirebaseAuth.getInstance().signOut();

                //Intent to main activity
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initUi() {
        user_name.setText(new StringBuilder()
        .append(Common.currentDeliveryAgentUser.getName()));
        user_phone.setText(new StringBuilder()
        .append(Common.currentDeliveryAgentUser.getPhone()));
        user_email.setText(new StringBuilder()
        .append(Common.currentDeliveryAgentUser.getEmail()));
        text_current_restaurant.setText(new StringBuilder()
        .append(Common.currentDeliveryAgentUser.getRestaurant()));
    }
}