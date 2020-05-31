package com.android.fgm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class sign extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        final EditText displayName = findViewById(R.id.displayName);
        final EditText number = findViewById(R.id.phoneNumber);
        Button sign =  findViewById(R.id.signButton);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verify = new Intent(getApplicationContext(), VerifyNumber.class);
                verify.putExtra("Name", displayName.getText().toString());
                verify.putExtra("Number", number.getText().toString());

                startActivity(verify);
            }
        });
    }
}
