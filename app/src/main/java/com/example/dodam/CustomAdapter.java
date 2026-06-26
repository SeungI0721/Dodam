// 커뮤니티 게시글 목록을 RecyclerView에 표시하는 Adapter 파일이다.
package com.example.dodam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        Post post = arrayList.get(position);
        String created = post.getCreatedAt() > 0
                ? new SimpleDateFormat("MM/dd HH:mm", Locale.KOREA).format(new Date(post.getCreatedAt()))
                : "";
        holder.tv_content.setText("[" + safe(post.getCategory()) + "] " + created + "\n" + post.getNoticeContent());
        holder.itemView.setOnLongClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || post.getAuthorUid() == null || !user.getUid().equals(post.getAuthorUid())) {
                return false;
            }
            new AlertDialog.Builder(context)
                    .setMessage("게시글을 삭제하시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("삭제", (dialog, which) ->
                            FirebaseDatabase.getInstance().getReference("Post")
                                    .child(post.getPostId())
                                    .removeValue())
                    .show();
            return true;
        });
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

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "FREE" : value;
    }
}
