package com.example.dodam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Post> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getUserPhoto())
                .into(holder.userPhoto);
        holder.tv_name.setText(arrayList.get(position).getUserName());
        holder.tv_time.setText(arrayList.get(position).getTime());
        holder.tv_Title.setText(arrayList.get(position).getNoticeTitle());
    }

    @Override
    public int getItemCount() {
        return (arrayList !=null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto;
        TextView tv_name, tv_time, tv_Title;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userPhoto = itemView.findViewById(R.id.userPhoto);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_time = itemView.findViewById(R.id.tv_time);
            this.tv_Title = itemView.findViewById(R.id.tv_Title);
        }
    }
}
