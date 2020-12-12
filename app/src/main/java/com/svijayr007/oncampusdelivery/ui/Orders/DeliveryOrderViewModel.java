package com.svijayr007.oncampusdelivery.ui.Orders;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.svijayr007.oncampusdelivery.callback.IDeliveryOrderCallbackListener;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.model.DeliveryOrderModel;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOrderViewModel extends ViewModel implements IDeliveryOrderCallbackListener {
    private MutableLiveData<List<DeliveryOrderModel>> DeliveryOrderMutableLiveData;
    private MutableLiveData<String> messageError;

    private IDeliveryOrderCallbackListener listener;

    public DeliveryOrderViewModel() {
        DeliveryOrderMutableLiveData = new MutableLiveData<>();
        messageError = new MutableLiveData<>();
        listener = this;
    }


    public MutableLiveData<List<DeliveryOrderModel>> getDeliveryOrderMutableLiveData(int status) {
        loadDeliveryOrder(status);
        return DeliveryOrderMutableLiveData;
    }

    private void loadDeliveryOrder(int status) {
        List<DeliveryOrderModel> tempList = new ArrayList<>();
        Query deliveryOrderRef = FirebaseDatabase.getInstance(Common.deliveryOrdersDB)
                                .getReference(Common.DELIVERY_ORDER_REF)
                                .orderByChild("agentId")
                                .equalTo(Common.currentDeliveryAgentUser.getAgentId());
        deliveryOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(tempList.size() > 0)
                        tempList.clear();

                    for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                        DeliveryOrderModel deliveryOrderModel = itemSnapshot.getValue(DeliveryOrderModel.class);
                        deliveryOrderModel.setKey(itemSnapshot.getKey());
                        if(deliveryOrderModel.getDeliveryStatus() == status){
                            tempList.add(deliveryOrderModel);
                        }
                    }
                    listener.onDeliveryOrderLoadSuccess(tempList);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onOrderLoadFailure(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onDeliveryOrderLoadSuccess(List<DeliveryOrderModel> deliveryOrderModelList) {
        DeliveryOrderMutableLiveData.setValue(deliveryOrderModelList);
    }

    @Override
    public void onOrderLoadFailure(String message) {
        messageError.setValue(message);
    }
}
