package com.android.fgm;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> messageAdapter;

    public void displayChatMessage(){
        ListView messageList =  findViewById(R.id.messageList);

        messageAdapter = new FirebaseListAdapter<ChatMessage>(
                this,
                ChatMessage.class,
                R.layout.message,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView messageText =  v.findViewById(R.id.messageText);
                TextView messageUser =  v.findViewById(R.id.messageUser);
                TextView messageTime =  v.findViewById(R.id.messageTime);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                messageTime.setText(DateFormat.format("hh:mm a", model.getMessageTime()));

            }
        };

        messageList.setAdapter(messageAdapter);
    }



    private void uploadMyNotificationToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Could not get token", Toast.LENGTH_SHORT).show();
                            Log.d("NotificationError",task.getException().toString());

                            return;
                        }

                        String token = task.getResult().getToken();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        FirebaseDatabase.getInstance().getReference("Members").child(uid).setValue(token);


                    }
                });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayChatMessage();

        uploadMyNotificationToken();

        Button sendMessage = findViewById(R.id.sendButton);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText messageText = findViewById(R.id.inputMessage);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(
                                messageText.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                        ));

                getTokens();

                messageText.setText("");



            }
        });
    }



    private void getTokens(){


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Members");

        ValueEventListener valueEventListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String token = ds.getValue(String.class);
                    Log.d("NotfiSend", "Token is= " +token);

                    buildNotification(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        ref.addListenerForSingleValueEvent(valueEventListener);

    }

    private void buildNotification(String token) {


        String title =  "New message";
        String body = "There is a new message in the chat";

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            Log.d("Notification", "Start: "  );

            notificationBody.put("title", title);
            notificationBody.put("body", body);

            notification.put("to", token);
            notification.put("notification", notificationBody);
        } catch (JSONException e) {
            Log.d("Notification", "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {

        Log.d("Notification", "JSON Sending"  );

        String FCM_API = "https://fcm.googleapis.com/fcm/send";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Notificationt", "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.d("Notificationt", "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                //Todo Add Server Key

                String key = "YOUR KEY COMES HERE";
                params.put("Authorization", "key="+ key);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
