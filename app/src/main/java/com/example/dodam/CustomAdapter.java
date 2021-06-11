package com.example.dodam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tv_userName.setText(arrayList.get(position).getUserName());
        holder.tv_title.setText(arrayList.get(position).getNoticeTitle());
        holder.tv_content.setText(arrayList.get(position).getNoticeContent());
    }

    @Override
    public int getItemCount() {

        return (arrayList !=null ? arrayList.size() : 0);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_userName;
        public TextView tv_title;
        public TextView tv_content;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_userName = itemView.findViewById(R.id.tv_userName);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_content = itemView.findViewById(R.id.tv_content);

        }
    }
}
