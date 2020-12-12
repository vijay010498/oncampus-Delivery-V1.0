package com.svijayr007.oncampusdelivery.callback;

import com.svijayr007.oncampusdelivery.model.RestaurantModel;

import java.util.List;

public interface IRestaurantCallbackListener {
    void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList);
    void onRestaurantLoadFailed(String message);
}
