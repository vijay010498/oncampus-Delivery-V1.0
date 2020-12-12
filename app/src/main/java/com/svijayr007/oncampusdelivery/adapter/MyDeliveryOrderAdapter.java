package com.svijayr007.oncampusdelivery.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.callback.IRecyclerClickListener;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.model.DeliveryOrderModel;
import com.svijayr007.oncampusdelivery.model.FCMResponse;
import com.svijayr007.oncampusdelivery.model.FCMSendData;
import com.svijayr007.oncampusdelivery.remote.IFCMService;
import com.svijayr007.oncampusdelivery.remote.RetroFitFCMClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyDeliveryOrderAdapter extends RecyclerView.Adapter<MyDeliveryOrderAdapter.myViewHolder> {
    Context context;
    List<DeliveryOrderModel> deliveryOrderModelList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private KProgressHUD hud;
    private AlertDialog dialogsOtp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IFCMService ifcmService = RetroFitFCMClient.getInstance().create(IFCMService.class);


    public MyDeliveryOrderAdapter(Context context, List<DeliveryOrderModel> deliveryOrderModelList) {
        this.context = context;
        this.deliveryOrderModelList = deliveryOrderModelList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .setCancellable(false)
                .setLabel("Loading")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_delivery_order_items,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        //Set data
        holder.txt_order_name.setText(new StringBuilder()
        .append(deliveryOrderModelList.get(position).getOrderModel().getUserName()));

        holder.txt_order_number.setText(new StringBuilder()
        .append(deliveryOrderModelList.get(position).getKey()));

        holder.txt_delivery_location.setText(new StringBuilder()
        .append(deliveryOrderModelList.get(position).getOrderModel().getShippingAddress()));

        //Order items
        int number_of_items = deliveryOrderModelList.get(position).getOrderModel().getCartItemList().size();
        holder.txt_order_items.setText("");
        for(int i=0;i<number_of_items;i++){
            holder.txt_order_items.append(new StringBuilder()
                    .append(deliveryOrderModelList.get(position).getOrderModel().getCartItemList().get(i).getFoodName())
                    .append(" x ")
                    .append(deliveryOrderModelList.get(position).getOrderModel().getCartItemList().get(i).getFoodQuantity()));

            if(i+1 < number_of_items)
                holder.txt_order_items.append(", \n");
            else
                holder.txt_order_items.append(".");
        }
        //Order date
        calendar.setTimeInMillis(deliveryOrderModelList.get(position).getOrderModel().getCreateDate());
        Date date = new Date((deliveryOrderModelList.get(position).getOrderModel().getCreateDate()));
        holder.txt_order_date.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
                .append(" ").append(simpleDateFormat.format(date)));


        //Delivery Layout
        if(deliveryOrderModelList.get(position).getDeliveryStatus() == 0){
            holder.delivery_layout.setVisibility(View.VISIBLE);
        }
        else {
            holder.delivery_layout.setVisibility(View.GONE);
        }


        //Call user
        holder.txt_call_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse(new StringBuilder("tel:").append(deliveryOrderModelList.get(position)
                                        .getOrderModel().getUserPhone()).toString()));
                                context.startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Toast.makeText(context, "You must enable this permission to call user", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //Deliver Now
        holder.txt_deliver_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);
                builder.setTitle("Complete Delivery");
                View v = LayoutInflater.from(context).inflate(R.layout.layout_delivery_otp,null);
                PinView optPin = v.findViewById(R.id.deliveryOtp);
                optPin.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(editable.toString().length() == 6){
                            hud.show();
                            //Check OTP
                            if(editable.toString().equals(deliveryOrderModelList.get(position).getOrderModel().getOTP())){
                                dialogsOtp.dismiss();
                                //First Update Orders DB
                                Map<String, Object> updateStatus = new HashMap<>();
                                updateStatus.put("orderStatus",3);
                                FirebaseDatabase.getInstance(Common.ordersDB)
                                        .getReference(Common.ORDER_REF)
                                        .child(deliveryOrderModelList.get(position).getKey())
                                        .updateChildren(updateStatus)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //Update Delivery Orders Db
                                                    Map<String, Object> updateDelivery = new HashMap<>();
                                                    updateDelivery.put("deliveryStatus",1);
                                                    FirebaseDatabase.getInstance(Common.deliveryOrdersDB)
                                                            .getReference(Common.DELIVERY_ORDER_REF)
                                                            .child(deliveryOrderModelList.get(position).getKey())
                                                            .updateChildren(updateDelivery)
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    hud.dismiss();
                                                                    Toast.makeText(context, "Delivery Db Update:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            hud.dismiss();
                                                            Toast.makeText(context, "All Successs", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                }
                                                else {
                                                    hud.dismiss();
                                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hud.dismiss();
                                        Toast.makeText(context, "Orders DB"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                            else {
                                hud.dismiss();
                                optPin.setError("Wrong OTP Enter Again");
                                optPin.setText("");
                                return;
                            }
                        }

                    }
                });
                builder.setView(v);
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogsOtp = builder.create();
                dialogsOtp.show();

            }
        });

        //Send User Notification
        holder.txt_notify_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hud.show();
                Map<String, String> notification = new HashMap<>();
                notification.put("title",new StringBuilder()
                .append(Common.currentDeliveryAgentUser.getName())
                .append("")
                .append("Reached Your Location").toString());
                notification.put("body","Delivery Agent has reached your location, Please collect your Order!");
                FCMSendData sendData = new FCMSendData(deliveryOrderModelList.get(position).getOrderModel().getUserToken(),notification);

                compositeDisposable.add(ifcmService.sendNotification(sendData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FCMResponse>() {
                    @Override
                    public void accept(FCMResponse fcmResponse) throws Exception {
                        holder.txt_notify_user.setText(new StringBuilder()
                        .append("Notify Again"));
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        hud.dismiss();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, "FCM ERROR"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        hud.dismiss();
                    }
                }));



            }
        });

        holder.setRecyclerClickListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                    Toast.makeText(context, "Order Clicked", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return deliveryOrderModelList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_order_name)
        TextView txt_order_name;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_delivery_location)
        TextView txt_delivery_location;
        @BindView(R.id.txt_order_items)
        TextView txt_order_items;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.delivery_layout)
        LinearLayout delivery_layout;
        @BindView(R.id.txt_deliver_now)
        TextView txt_deliver_now;
        @BindView(R.id.txt_call_user)
        TextView txt_call_user;
        @BindView(R.id.txt_notify_user)
        TextView txt_notify_user;

        IRecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClickListener(view,getAdapterPosition());
        }
    }
}
