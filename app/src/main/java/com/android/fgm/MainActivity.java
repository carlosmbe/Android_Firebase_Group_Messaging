package com.android.fgm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

                messageTime.setText(DateFormat.format("hh:mm a", model.getMessageTime()));;

                FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
                String myid = me.getUid().toString();

                if(model.getUserId().matches(myid)){

                    messageText.setBackgroundResource(R.drawable.my_bubble);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)messageText.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageText.setLayoutParams(params);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)messageUser.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageUser.setLayoutParams(params2);

                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)messageTime.getLayoutParams();
                    params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageTime.setLayoutParams(params1);

                    int value1 = 10;

                    int value2 = 4;

                    int value3 = 20;

                    int spaceing = 2;


                    final float scale = getResources().getDisplayMetrics().density;

                    int dpValue1 = (int) (value1 * scale + 0.5f);

                    int dpValue2 = (int) (value2 * scale + 0.5f);

                    int dpValue3 = (int) (value3 * scale + 0.5f);

                    int space = (int) (spaceing * scale );

                    messageText.setPadding(dpValue1,dpValue2,dpValue3,dpValue1);


                    messageText.setLineSpacing(space, 0.5f);

                }
            }
        };

        messageList.setAdapter(messageAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayChatMessage();

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

                messageText.setText("");

            }
        });
    }
}
