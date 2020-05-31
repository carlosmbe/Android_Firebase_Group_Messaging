package com.android.fgm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
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
