package com.svijayr007.oncampusdelivery.ui.Orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.adapter.MyDeliveryOrderAdapter;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrdersFragment extends Fragment {
    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;
    MyDeliveryOrderAdapter adapter;
    private DeliveryOrderViewModel deliveryOrderViewModel;
    Unbinder unbinder;


    public OrdersFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders,container,false);
        unbinder = ButterKnife.bind(this, root);
        recycler_order.setHasFixedSize(true);
        recycler_order.setLayoutManager(new LinearLayoutManager(getContext()));
        deliveryOrderViewModel = new ViewModelProvider(this).get(DeliveryOrderViewModel.class);
        if(getArguments() != null){
            deliveryOrderViewModel.getMessageError().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
                }
            });
            deliveryOrderViewModel.getDeliveryOrderMutableLiveData(getArguments().getInt("orderStatus")).observe(getViewLifecycleOwner(), deliveryOrderModelList -> {
                if(deliveryOrderModelList != null){
                    Collections.reverse(deliveryOrderModelList);
                    adapter = new MyDeliveryOrderAdapter(getContext(),deliveryOrderModelList);
                    recycler_order.setAdapter(adapter);
                }
            });
        }
        return root;

    }
    public Fragment OrderFilter(int status){
        Log.i("STATUS", String.valueOf(status));
        OrdersFragment ordersFragment = new OrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("orderStatus",status);
        ordersFragment.setArguments(bundle);
        return ordersFragment;
    }




    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}