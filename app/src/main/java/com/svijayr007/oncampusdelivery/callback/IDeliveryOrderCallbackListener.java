package com.svijayr007.oncampusdelivery.callback;

import com.svijayr007.oncampusdelivery.model.DeliveryOrderModel;

import java.util.List;

public interface IDeliveryOrderCallbackListener {
    void onDeliveryOrderLoadSuccess(List<DeliveryOrderModel> deliveryOrderModelList);
    void onOrderLoadFailure(String message);
}
