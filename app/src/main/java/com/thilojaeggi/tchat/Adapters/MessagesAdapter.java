package com.thilojaeggi.tchat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.thilojaeggi.tchat.MessageModel;
import com.thilojaeggi.tchat.R;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<MessageModel> mData;
    private LayoutInflater mInflater;
    Context context;

    public MessagesAdapter(Context context, List<MessageModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        MessageModel messageModel = mData.get(position);
        holder.name.setText(messageModel.getName());
        holder.message.setText(messageModel.getMessage());
        holder.deviceId.setText(messageModel.getDeviceId());
        if (messageModel.getImageurl() != null && !messageModel.getImageurl().isEmpty()){
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(messageModel.getImageurl())).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, deviceId;
        ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
            deviceId = itemView.findViewById(R.id.deviceid);
        }

    }



}