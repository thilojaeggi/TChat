package com.thilojaeggi.tchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thilojaeggi.tchat.ChatActivity;
import com.thilojaeggi.tchat.R;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    SharedPreferences sharedPreferences;
    private List<String> mData;
    private LayoutInflater mInflater;
    Context context;
    // data is passed into the constructor
    public ChatsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String chatName = mData.get(position);
        holder.chatName_tv.setText(chatName);
        sharedPreferences= context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor= sharedPreferences.edit();
        holder.chat_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                editor.putString("chatname", chatName).apply();
                context.startActivity(intent);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatName_tv;
        CardView chat_cv;
        ViewHolder(View itemView) {
            super(itemView);
            chat_cv = itemView.findViewById(R.id.chat_cv);
            chatName_tv = itemView.findViewById(R.id.chatname);
        }

    }



}