package com.varanegar.vaslibrary.ui.fragment.new_fragment.ticket_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.newmodel.userticketmodel.UserTicketModels;

import java.io.File;
import java.util.List;

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserTicketModels> list;
    private Context context;

    public List<UserTicketModels> getList() {
        return list;
    }

    public void setList(List<UserTicketModels> list) {
        this.list = list;
    }

    public RecyclerChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTag();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case UserTicketModels.TAG_LEFT:
                view = LayoutInflater.from(context).inflate(R.layout.item_layout_chat_left, null);
                return new ChatLeftHolder(view);
            case UserTicketModels.TAG_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.item_layout_chat_right, null);
                return new ChatLeftHolder(view);
//            case UserTicketModels.TAG_FILE_LEFT:
//                view = LayoutInflater.from(context).inflate(R.layout.recycler_file_chat_left, null);
//                return new ChatFileLeftHolder(view);
//            case ChatInfo.TAG_FILE_RIGHT:
//                view = LayoutInflater.from(context).inflate(R.layout.recycler_file_chat_right, null);
//                return new ChatFileRightHolder(view);
        }
        return new ChatLeftHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String name = list.get(position).getName();
        final String content = list.get(position).getContent();
        switch (list.get(position).getTag()) {
            case UserTicketModels.TAG_LEFT:
                ChatLeftHolder chatLeftHolder = (ChatLeftHolder) holder;
                chatLeftHolder.getTvName().setText(name);
                chatLeftHolder.getTvContent().setText(content);
                break;
            case UserTicketModels.TAG_RIGHT:
                ChatRightHolder chatRightHolder = (ChatRightHolder) holder;
                chatRightHolder.getTvName().setText(name);
                chatRightHolder.getTvContent().setText(content);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
