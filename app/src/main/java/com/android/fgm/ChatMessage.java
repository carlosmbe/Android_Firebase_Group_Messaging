package com.android.fgm;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String userId;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser){

        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public ChatMessage(){

    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser(){
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}

