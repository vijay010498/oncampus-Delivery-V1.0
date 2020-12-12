package com.svijayr007.oncampusdelivery.ui.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.svijayr007.oncampusdelivery.MainActivity;
import com.svijayr007.oncampusdelivery.R;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String number = "";
    private ProgressDialog progressDialog;
    private TextView text_otp_sent;
    private MaterialButton button_login;
    private EditText edit_otp;
    private String storedVerificationId = "";
    private PhoneAuthCredential credential;
    private CountDownTimer countDownTimer;
    private TextView textResendOtp;
    private ImageView imageClose;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        getArgs();
        init();
        setListener();
        sendOtp(number);

    }
    private void sendOtp(String number) {
        progressDialog.setMessage("Sending OTP");
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks);

    }
    private void getArgs() {
        number = getIntent().getStringExtra("CUSTOMER_MOBILE");
        Log.i("NUMBER TEST",number);
    }
    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        text_otp_sent = findViewById(R.id.text_otp_sent);
        button_login = findViewById(R.id.button_login);
        edit_otp = findViewById(R.id.edit_otp);
        textResendOtp = findViewById(R.id.text_resend_otp);
        imageClose = findViewById(R.id.image_close);
        text_otp_sent.setText("Enter the six digit OTP which has been sent to your mobile number: " + number);
    }
    private void setListener() {
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edit_otp.getText().toString().isEmpty() && edit_otp.getText().toString().length() ==6){
                    if(!storedVerificationId.isEmpty()){
                        credential = PhoneAuthProvider.getCredential(storedVerificationId,edit_otp.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }
        });
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        countDownTimer = new CountDownTimer(1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textResendOtp.setText("Resend OTP(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                textResendOtp.setText("Resend OTP");
                textResendOtp.setEnabled(true);
            }
        };
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Toast.makeText(OtpActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                edit_otp.setText(phoneAuthCredential.getSmsCode());
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                edit_otp.setText("");
                textResendOtp.setEnabled(true);

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                storedVerificationId = s;
                resendingToken = forceResendingToken;
                textResendOtp.setEnabled(false);
                countDownTimer.start();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                textResendOtp.setEnabled(true);
                Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }
        };

        textResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(number, resendingToken);
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            //sign in failed
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                YoYo.with(Techniques.Shake)
                                        .duration(1000)
                                        .playOn(edit_otp);
                                Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                                edit_otp.setText("");
                            }
                        }
                        countDownTimer.cancel();
                        textResendOtp.setEnabled(true);
                    }
                });

    }
    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Cancel process?")
                .setMessage("Are you sure want to cancel the OTP process")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OtpActivity.super.onBackPressed();
                        dialog.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void resendVerificationCode(String number, PhoneAuthProvider.ForceResendingToken resendingToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendingToken
        );

    }

}