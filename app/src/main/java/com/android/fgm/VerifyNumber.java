package com.android.fgm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class VerifyNumber extends AppCompatActivity {

    FirebaseUser user;
    String mVerificationId;
    private FirebaseAuth mAuth;
    String displayname;

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+265" + mobile,
                0,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Toast.makeText(getApplicationContext(), "Verification Complete", Toast.LENGTH_SHORT).show();
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            Toast.makeText(getApplicationContext(), "Code Sent", Toast.LENGTH_SHORT).show();
            mVerificationId = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(VerifyNumber.this, "Sign Success", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayname).build();

                            user.updateProfile(profileUpdates);

                            Intent intent = new Intent(VerifyNumber.this, MainActivity.class);

                            startActivity(intent);

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(VerifyNumber.this, "Sign Failed", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        mAuth = FirebaseAuth.getInstance();

         user = FirebaseAuth.getInstance().getCurrentUser();


        Intent getDetail = getIntent();
        String number  = getDetail.getStringExtra("Number");
        displayname = getDetail.getStringExtra("Name");


        sendVerificationCode(number);

        final EditText verifyCode = findViewById(R.id.verifyCode);

        Button verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode.getText().toString());

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(VerifyNumber.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(VerifyNumber.this, "Verification Success", Toast.LENGTH_SHORT).show();
                                    if (user != null) {
                                        Intent intent = new Intent(VerifyNumber.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(VerifyNumber.this, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }

}
