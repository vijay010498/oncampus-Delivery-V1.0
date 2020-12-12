package com.svijayr007.oncampusdelivery.ui.Orders;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.svijayr007.oncampusdelivery.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersActivity extends AppCompatActivity {
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    String[] tabsTitles;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);
        initViewPager();

        hud =  KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark))
                .setCancellable(false)
                .setLabel("Processing")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
    }

    private void initViewPager() {
        List<Fragment> orderFilterList = new ArrayList<>();
        orderFilterList.add(new OrdersFragment().OrderFilter(0));
        orderFilterList.add(new OrdersFragment().OrderFilter(1));
        tabsTitles = new String[]{"Delivery Now", "Delivered"};
        viewPager.setAdapter(new TabsSetup(orderFilterList, getSupportFragmentManager(),tabsTitles));
        tabs.setupWithViewPager(viewPager);
    }
}