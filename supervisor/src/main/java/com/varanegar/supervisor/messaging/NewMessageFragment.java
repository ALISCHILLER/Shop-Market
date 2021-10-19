package com.varanegar.supervisor.messaging;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 7/1/2018.
 */

public class NewMessageFragment extends SlidingDialog {
    private List<VisitorModel> selectedVisitors = new ArrayList<>();
    private RecyclerView.Adapter<RecipientViewHolder> recipientsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_new_msg_layout, container, false);
        RecyclerView recipientsRecyclerView = view.findViewById(R.id.recipients_recycler_view);
        recipientsAdapter = new RecyclerView.Adapter<RecipientViewHolder>() {

            @Override
            public RecipientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipient_item_layout, parent, false);
                return new RecipientViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecipientViewHolder holder, int position) {
                ((RecipientViewHolder) holder).bindView(position);
            }

            @Override
            public int getItemCount() {
                return selectedVisitors.size();
            }
        };
        recipientsRecyclerView.setAdapter(recipientsAdapter);
        recipientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ImageView recipientImageView = view.findViewById(R.id.recipients_image_view);
        recipientImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisitorManager visitorManager = new VisitorManager(getContext());
                List<VisitorModel> visitors = visitorManager.getAll();
                final SearchBox<VisitorModel> searchBox = new SearchBox<>();
                searchBox.isMultiSelect(true);
                searchBox.setItems(visitors, new SearchBox.SearchMethod<VisitorModel>() {
                    @Override
                    public boolean onSearch(VisitorModel item, String text) {
                        return item.Name != null && item.Name.contains(text);
                    }
                });

                searchBox.setOnSelectionFinishedListener(new SearchBox.OnSelectionFinishedListener<VisitorModel>() {
                    @Override
                    public void onDone(List<VisitorModel> selectedItems) {
                        selectedVisitors = selectedItems;
                        recipientsAdapter.notifyDataSetChanged();
                        searchBox.dismiss();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                searchBox.show(getChildFragmentManager(), "SearchBox<VisitorModel>");
            }
        });
        return view;
    }

    class RecipientViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;
        private final ImageView deleteImageView;

        public RecipientViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user_name_text_view);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);

        }

        public void bindView(final int position) {
            VisitorModel user = selectedVisitors.get(position);
            usernameTextView.setText(user.Name);
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedVisitors.remove(position);
                    recipientsAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
