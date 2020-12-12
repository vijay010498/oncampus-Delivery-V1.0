package com.svijayr007.oncampusdelivery.eventBus;

import com.svijayr007.oncampusdelivery.model.RestaurantModel;

public class RestaurantSelectedEvent {
    private RestaurantModel restaurantModel;

    public RestaurantSelectedEvent(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }

    public RestaurantModel getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }
}
