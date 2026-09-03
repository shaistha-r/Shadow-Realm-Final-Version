package com.example.loginapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends RealmActivity {
    private String room;
    private TextView messages;
    private EditText input;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final Runnable poller=()->load();
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);setContentView(R.layout.activity_chat);
        setupTopBar("COUNCIL CHAT","PUBLIC DISCUSSION • NEVER REVEAL THE SECRET","CHAT");
        messages=findViewById(R.id.txtMessages);input=findViewById(R.id.etMessage);
        room=getIntent().getStringExtra("ROOM_CODE");if(room==null)room=OnlineSession.room(this);
        findViewById(R.id.btnSend).setOnClickListener(v->send());
        load();
    }
    private void load(){
        if(room==null)return;
        RetrofitClient.getApiService(this).chat(room).enqueue(new Callback<List<ChatResponse>>(){
            @Override public void onResponse(Call<List<ChatResponse>> c,Response<List<ChatResponse>> r){
                if(r.isSuccessful()&&r.body()!=null){
                    StringBuilder sb=new StringBuilder();
                    for(ChatResponse m:r.body())sb.append(m.getUsername()).append("  •  ").append(m.getMessage()).append("\n\n");
                    messages.setText(sb.length()==0?"The council is silent. Begin the discussion.":sb.toString().trim());
                    ScrollView s=findViewById(R.id.chatScroll);s.post(()->s.fullScroll(ScrollView.FOCUS_DOWN));
                }
                handler.removeCallbacks(poller);handler.postDelayed(poller,2000);
            }
            @Override public void onFailure(Call<List<ChatResponse>> c,Throwable t){handler.removeCallbacks(poller);handler.postDelayed(poller,3000);}
        });
    }
    private void send(){
        String text=input.getText().toString().trim();
        if(text.isEmpty())return;
        findViewById(R.id.btnSend).setEnabled(false);
        RetrofitClient.getApiService(this).sendChat(room,new ChatRequest(text)).enqueue(new Callback<ChatResponse>(){
            @Override public void onResponse(Call<ChatResponse> c,Response<ChatResponse> r){
                findViewById(R.id.btnSend).setEnabled(true);
                if(r.isSuccessful()){input.setText("");load();}else Ui.toast(ChatActivity.this,"The realm moderator blocked that message or chat is closed.");
            }
            @Override public void onFailure(Call<ChatResponse> c,Throwable t){findViewById(R.id.btnSend).setEnabled(true);Ui.toast(ChatActivity.this,"Cannot send the message.");}
        });
    }
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy();}
}
