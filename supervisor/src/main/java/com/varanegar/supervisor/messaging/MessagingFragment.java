package com.varanegar.supervisor.messaging;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.supervisor.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by A.Torabi on 6/23/2018.
 */

public class MessagingFragment extends VaranegarFragment {
    private BaseRecyclerView msgRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messaging_layout, container, false);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewMessageFragment fragment = new NewMessageFragment();
                fragment.show(getChildFragmentManager(),"NewMessageFragment");
            }
        });
        msgRecyclerView = view.findViewById(R.id.messages_recycler_view);
        final BaseRecyclerAdapter<MessageSummaryViewModel> adapter = new BaseRecyclerAdapter<MessageSummaryViewModel>(getVaranegarActvity(), getMessages()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_summary_layout, parent, false);
                return new MessageSummaryViewHolder(view, this, getContext());
            }
        };
        msgRecyclerView.setAdapter(adapter);
        return view;
    }

    public List<MessageSummaryViewModel> getMessages() {
        List<MessageSummaryViewModel> messages = new ArrayList<>();
        MessageSummaryViewModel msg1 = new MessageSummaryViewModel();
        msg1.Title = "title 1";
        msg1.RecipientName = "Ali";
        msg1.RecipientType = RecipientType.Single;
        msg1.SendDate = new Date();
        msg1.Status = MessageStatus.Received;
        messages.add(msg1);

        MessageSummaryViewModel msg2 = new MessageSummaryViewModel();
        msg2.Title = "title 2";
        msg2.RecipientName = "Omid";
        msg2.RecipientType = RecipientType.Group;
        msg2.SendDate = new Date();
        msg2.Status = MessageStatus.Failed;
        messages.add(msg2);

        return messages;
    }

    public class MessageSummaryViewHolder extends BaseViewHolder<MessageSummaryViewModel> {

        private final ImageView recipientTypeImageView;
        private final TextView recipientNameTextView;
        private final TextView titleTextView;
        private final TextView sendDateTextView;
        private final ImageView statusImageView;

        public MessageSummaryViewHolder(View itemView, BaseRecyclerAdapter<MessageSummaryViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            recipientTypeImageView = itemView.findViewById(R.id.recipient_type_image_view);
            recipientNameTextView = itemView.findViewById(R.id.recipient_name_text_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            sendDateTextView = itemView.findViewById(R.id.send_date_text_view);
            statusImageView = itemView.findViewById(R.id.status_image_view);
        }

        @Override
        public void bindView(int position) {
            MessageSummaryViewModel msg = recyclerAdapter.get(position);
            recipientNameTextView.setText(msg.RecipientName);
            titleTextView.setText(msg.Title);
            sendDateTextView.setText(DateHelper.toString(msg.SendDate, DateFormat.Complete, Locale.getDefault()));
            if (msg.Status == MessageStatus.Received)
                statusImageView.setImageResource(R.drawable.ic_done_all_green_900_24dp);
            else if (msg.Status == MessageStatus.Sent)
                statusImageView.setImageResource(R.drawable.ic_done_green_900_24dp);
            else
                statusImageView.setImageResource(R.drawable.ic_error_outline_red_900_24dp);

            if (msg.RecipientType == RecipientType.Group)
                recipientTypeImageView.setImageResource(R.drawable.ic_multi_user_cyan_24dp);
            else
                recipientTypeImageView.setImageResource(R.drawable.ic_single_user_cyan_24dp);
        }
    }
}
