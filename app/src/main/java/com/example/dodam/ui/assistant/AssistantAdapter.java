// 도담이 채팅 메시지 목록을 RecyclerView에 연결하는 Adapter 파일이다.
package com.example.dodam.ui.assistant;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dodam.R;

import java.util.ArrayList;
import java.util.List;

// 사용자 메시지와 도담이 메시지를 서로 다른 정렬과 배경으로 표시한다.
public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.MessageViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    // 새 메시지 목록을 받아 RecyclerView를 다시 그린다.
    public void submitMessages(List<ChatMessage> nextMessages) {
        messages.clear();
        if (nextMessages != null) {
            messages.addAll(nextMessages);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    // 메시지 아이템 레이아웃을 생성한다.
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assistant_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    // 메시지 작성 주체에 따라 정렬, 배경, 글자색을 적용한다.
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.messageText.setText(message.getText());
        holder.container.setGravity(message.isFromUser() ? Gravity.END : Gravity.START);
        holder.messageText.setBackgroundResource(message.isFromUser()
                ? R.drawable.bg_assistant_user_message
                : R.drawable.bg_assistant_bot_message);
        holder.messageText.setTextColor(message.isFromUser() ? 0xFFFFFFFF : 0xFF1E293B);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // 메시지 아이템 안의 컨테이너와 텍스트 뷰를 보관한다.
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout container;
        private final TextView messageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.message_container);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }
}
