package com.svijayr007.oncampusdelivery.ui.signup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svijayr007.oncampusdelivery.callback.IRestaurantCallbackListener;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.model.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel implements IRestaurantCallbackListener {
    private MutableLiveData<List<RestaurantModel>>  restaurantListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();

    private IRestaurantCallbackListener listener;

    public RestaurantViewModel() {
        listener = this;
    }

    public MutableLiveData<List<RestaurantModel>> getRestaurantListMutable() {
        if(restaurantListMutable == null){
            restaurantListMutable = new MutableLiveData<>();
            loadRestaurantFromFirebase();
        }
        return restaurantListMutable;
    }

    private void loadRestaurantFromFirebase() {
        List<RestaurantModel> restaurantModels = new ArrayList<>();
        FirebaseDatabase.getInstance(Common.restaurantDB)
                .getReference(Common.RESTAURANT_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot restaurantSnapShot:dataSnapshot.getChildren()){
                                RestaurantModel restaurantModel = restaurantSnapShot.getValue(RestaurantModel.class);
                                restaurantModel.setId(restaurantSnapShot.getKey());
                                restaurantModels.add(restaurantModel);
                            }
                            if(restaurantModels.size() > 0)
                                listener.onRestaurantLoadSuccess(restaurantModels);
                            else
                                listener.onRestaurantLoadFailed("Restaurant List empty");

                        }else {
                            listener.onRestaurantLoadFailed("No Restaurant Found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList) {
        restaurantListMutable.setValue(restaurantModelList);

    }

    @Override
    public void onRestaurantLoadFailed(String message) {
        messageError.setValue(message);
    }
}