package com.svijayr007.oncampusdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.model.DeliveryOrderModel;
import com.svijayr007.oncampusdelivery.ui.Orders.OrdersActivity;
import com.svijayr007.oncampusdelivery.ui.account.AccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @BindView(R.id.orders_for_delivery)
    TextView orders_for_delivery;
    @BindView(R.id.orders_delivered)
    TextView orders_delivered;
    @BindView(R.id.delivery_agent_name)
    TextView delivery_agent_name;

    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        hud =  KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark))
                .setCancellable(false)
                .setLabel("Processing")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        setUiData();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_delivery:
                        startActivity(new Intent(HomeActivity.this, OrdersActivity.class));
                        break;
                    case R.id.nav_account:
                        startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                        break;
                }
                return true;
            }
        });


    }

    private void setUiData() {
        hud.show();
        delivery_agent_name.setText(new StringBuilder()
        .append("Welcome ")
        .append(Common.currentDeliveryAgentUser.getName()));

        //Calculate delivery order's count
        FirebaseDatabase.getInstance(Common.deliveryOrdersDB)
                .getReference(Common.DELIVERY_ORDER_REF)
                .orderByChild("agentId")
                .equalTo(Common.currentDeliveryAgentUser.getAgentId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int for_delivery = 0;
                        int delivered = 0;

                        for(DataSnapshot orderSnapshot : snapshot.getChildren()){
                            DeliveryOrderModel liveModel = orderSnapshot.getValue(DeliveryOrderModel.class);
                            if(liveModel.getDeliveryStatus() == 0){
                                for_delivery++;
                            }
                            else if(liveModel.getDeliveryStatus() == 1){
                                delivered++;
                            }
                        }
                        orders_for_delivery.setText(new StringBuilder()
                        .append(for_delivery));

                        orders_delivered.setText(new StringBuilder()
                        .append(delivered));
                        hud.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hud.dismiss();
                        Toast.makeText(HomeActivity.this, "Order's Count:"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}