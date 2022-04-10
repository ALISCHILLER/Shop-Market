package com.varanegar.supervisor.fragment.list_pincode_Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.ui.foldingcell.FoldingCell;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.fragment.list_notification_Fragment.ListNotificationAdapter;

public class ListPinCodeAdapter extends  RecyclerView.Adapter<ListPinCodeAdapter.ViewHolder> {


    public ListPinCodeAdapter(Context context){
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_listpincode_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.layout_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.folding_cell.toggle(false);
            }
        });
        holder.layout_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.folding_cell.toggle(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private FoldingCell folding_cell;
        private TextView txt_name_customer,txt_customer_code,txt_date;
        private LinearLayout layout_header,layout_sub;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_name_customer=itemView.findViewById(R.id.text_customerName);
            txt_customer_code=itemView.findViewById(R.id.txt_customer_code);
            txt_date=itemView.findViewById(R.id.txt_date);
            layout_header=itemView.findViewById(R.id.layout_header);
            layout_sub=itemView.findViewById(R.id.layout_sub);
            folding_cell=itemView.findViewById(R.id.folding_cell);
        }
    }
}
