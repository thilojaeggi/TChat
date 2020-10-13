package com.thilojaeggi.tchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thilojaeggi.tchat.Adapters.ChatsAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> chatList;
    ChatsAdapter chatsAdapter;
    EditText search_et;
    RecyclerView chats_rv;
    ImageButton settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        settings = findViewById(R.id.settingsbutton);
        chatList = new ArrayList<>();
        chats_rv = findViewById(R.id.chats_rv);
        search_et = findViewById(R.id.search_et);
        chats_rv.setLayoutManager(new LinearLayoutManager(this));
        getChats();
        chatsAdapter = new ChatsAdapter(this, chatList);
        chats_rv.setAdapter(chatsAdapter);
        chatList.clear();
        final SharedPreferences sharedPreferences= this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        boolean firstart = sharedPreferences.getBoolean("firstart", true);
        if (firstart){
            Intent firststartintent = new Intent(MainActivity.this, SetName.class);
            startActivity(firststartintent);
        }
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchChat(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        String versionName = com.thilojaeggi.tchat.BuildConfig.VERSION_NAME;
        TextView credits = findViewById(R.id.credits);
        credits.setText("Version " + versionName + " © 2020 Thilo Jaeggi");
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getChats() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatKey = snapshot.getKey().toString();
                    chatList.add(chatKey);
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void searchChat(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByKey()
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String chatKey = snapshot.getKey().toString();
                    chatList.add(chatKey);
                }
                chatsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}