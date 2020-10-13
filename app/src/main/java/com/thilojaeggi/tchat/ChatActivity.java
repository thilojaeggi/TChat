package com.thilojaeggi.tchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.tchat.Adapters.MessagesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    MessagesAdapter adapter;
    EditText message;
    List<MessageModel> meModels;
    ImageButton send;
    ImageView back;
    TextView chatNameTitle;
    String chatname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        final SharedPreferences sharedPreferences= this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        chatname = sharedPreferences.getString("chatname", "null");
        chatNameTitle = findViewById(R.id.chatname);
        chatNameTitle.setText(chatname);
        //TODO Allow users to create groups
        final RecyclerView messages_rv = findViewById(R.id.messages_rv);
        meModels = new ArrayList<>();
        meModels.clear();
        back = findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send = findViewById(R.id.postbutton);
        message = findViewById(R.id.add_message);
        ImageButton attach = findViewById(R.id.attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttachmentBottomSheet addPhotoBottomDialogFragment =
                        AttachmentBottomSheet.newInstance();
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                        AttachmentBottomSheet.TAG);
            }
        });
        messages_rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(this, meModels);
        messages_rv.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().isEmpty()){
                    if (message.getText().toString().equals("/clear")){
                        meModels.clear();
                        adapter.notifyDataSetChanged();
                        FirebaseDatabase.getInstance().getReference().removeValue();
                    } else {
                        MessageModel meMod = new MessageModel();
                        String deviceId = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        meMod.setName(sharedPreferences.getString("name", null));
                        meMod.setMessage(message.getText().toString());
                        meMod.setDeviceId(deviceId);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", sharedPreferences.getString("name", null));
                        hashMap.put("message", message.getText().toString());
                        hashMap.put("deviceId", deviceId);
                        hashMap.put("imageurl", "");
                        reference.child(chatname).child("messages").child(""+System.currentTimeMillis()).setValue(hashMap);
                        meModels.add(meMod);
                        adapter.notifyDataSetChanged();
                        message.setText("");
                    }
                }
            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                messages_rv.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        String chatname = sharedPreferences.getString("chatname", "null");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatname).child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                meModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = snapshot.getValue(MessageModel.class);
                    if (message.getMessage() != null && !message.getName().isEmpty()) {
                        meModels.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}