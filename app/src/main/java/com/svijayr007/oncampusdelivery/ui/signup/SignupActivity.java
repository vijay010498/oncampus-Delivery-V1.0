package com.svijayr007.oncampusdelivery.ui.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.svijayr007.oncampusdelivery.MainActivity;
import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.adapter.MyRestaurantAdapter;
import com.svijayr007.oncampusdelivery.common.Common;
import com.svijayr007.oncampusdelivery.common.SpacesItemDecoration;
import com.svijayr007.oncampusdelivery.eventBus.RestaurantSelectedEvent;
import com.svijayr007.oncampusdelivery.model.DeliveryAgentUserModel;
import com.svijayr007.oncampusdelivery.model.RestaurantModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class SignupActivity extends AppCompatActivity {
    private EditText edtAgentName;
    private TextView gmail;
    private TextView text_restaurant_name;
    private ImageView closeImage;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private MaterialButton buttonRegister;
    private DatabaseReference deliveryAgentRef;

    private static RestaurantModel selectedRestaurant;

    private KProgressHUD hud;

    //Restaurant
    private RestaurantViewModel restaurantViewModel;
    private AlertDialog dialogrestaurant;

    //Google SIgn in
    private static  final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mgoogleSignInClient;
    private static String googleIdToken ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        restaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);
        init();
        setListener();

    }

    private void setListener() {
        //for close image
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        text_restaurant_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                    builder.setTitle(new StringBuilder()
                    .append("Select Restaurant "));
                    View v = LayoutInflater.from(SignupActivity.this).inflate(R.layout.layout_restaurant, null);
                    RecyclerView recycler_restaurant = v.findViewById(R.id.recycler_restaurant);
                    restaurantViewModel.getMessageError().observe(SignupActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            Toast.makeText(SignupActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                    restaurantViewModel.getRestaurantListMutable().observe(SignupActivity.this, new Observer<List<RestaurantModel>>() {
                        @Override
                        public void onChanged(List<RestaurantModel> restaurantModels) {
                            MyRestaurantAdapter restaurantAdapter = new MyRestaurantAdapter(SignupActivity.this, restaurantModels);
                            recycler_restaurant.setAdapter(restaurantAdapter);
                            recycler_restaurant.setLayoutManager(new LinearLayoutManager(SignupActivity.this));
                            recycler_restaurant.setVerticalScrollBarEnabled(true);
                            recycler_restaurant.addItemDecoration(new SpacesItemDecoration(5));
                        }
                    });
                    builder.setView(v);
                    dialogrestaurant = builder.create();
                    dialogrestaurant.show();

            }
        });

        //Google signIn
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mgoogleSignInClient != null){
                    mgoogleSignInClient.signOut();
                    gmail.setText("");
                }
                Intent signInIntent = mgoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        //For Register Button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtAgentName.getText().toString())){
                    edtAgentName.setError("Name Required");
                    return;
                }
                else if(TextUtils.isEmpty(gmail.getText().toString()) ||
                        !isValidMail(gmail.getText().toString())){
                    gmail.setError("Email Empty or not valid");
                    return;
                }

                else  if(TextUtils.isEmpty(text_restaurant_name.getText().toString())){
                    text_restaurant_name.setError("Please Select restaurant");
                    return;
                }
                hud.show();
                AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken,null);
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    DeliveryAgentUserModel deliveryAgentUserModel = new DeliveryAgentUserModel();
                                    deliveryAgentUserModel.setActive(false); // Default False
                                    deliveryAgentUserModel.setEmail(gmail.getText().toString());
                                    deliveryAgentUserModel.setName(edtAgentName.getText().toString());
                                    deliveryAgentUserModel.setPhone(user.getPhoneNumber());
                                    deliveryAgentUserModel.setAgentId(user.getUid());
                                    deliveryAgentUserModel.setLastVisited(System.currentTimeMillis());
                                    deliveryAgentUserModel.setPartner(selectedRestaurant.getPartner());
                                    deliveryAgentUserModel.setRestaurant(selectedRestaurant.getId());


                                    deliveryAgentRef.child(user.getUid()).setValue(deliveryAgentUserModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        hud.dismiss();
                                                        Toast.makeText(SignupActivity.this,"Registration Success",Toast.LENGTH_SHORT).show();
                                                        goToMainActivity(deliveryAgentUserModel);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hud.dismiss();
                                            Toast.makeText(SignupActivity.this, "Creating Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hud.dismiss();
                        if(e.getMessage().contains("already associated with a different")){
                            Toast.makeText(SignupActivity.this, "Email Account Already Added with different account", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(SignupActivity.this, "Linking Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });




    }

    private void goToMainActivity(DeliveryAgentUserModel deliveryAgentUserModel) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Common.currentDeliveryAgentUser = deliveryAgentUserModel;
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                Common.currentDeliveryAgentUser = deliveryAgentUserModel;
                Common.createToken(SignupActivity.this,task.getResult().getToken());
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();

            }
        });
    }

    private void init() {
        hud = KProgressHUD.create(SignupActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .setCancellable(true)
                .setLabel("Creating Account")
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        edtAgentName = findViewById(R.id.edit_delivery_agent_name);
        text_restaurant_name = findViewById(R.id.text_restaurant_name);
        gmail = findViewById(R.id.gmail);
        closeImage = findViewById(R.id.image_close);
        mAuth  = FirebaseAuth.getInstance();
        user  = mAuth.getCurrentUser();
        buttonRegister = findViewById(R.id.button_register);
        deliveryAgentRef = FirebaseDatabase.getInstance(Common.deliveryAgentsDB).getReference(Common.DELIVERY_AGENT_REF);

        //Google signin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mgoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult();
                googleIdToken = account.getIdToken();
                gmail.setText(new StringBuilder()
                        .append(account.getEmail()));

            }catch (Exception e){
                if(e.getMessage().contains("12501")){
                    Toast.makeText(this, "No Email selected", Toast.LENGTH_SHORT).show();
                    gmail.setHint("Link Email!");
                }
                else
                    Toast.makeText(this, "Gmail Error"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignupActivity.this)
                .setTitle("Cancel Process?")
                .setMessage("Are you sure want to cancel the registration process?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignupActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRestaurantClicked(RestaurantSelectedEvent event){
        selectedRestaurant = event.getRestaurantModel();
        text_restaurant_name.setText(new StringBuilder()
        .append(event.getRestaurantModel().getName()));
        dialogrestaurant.dismiss();
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}