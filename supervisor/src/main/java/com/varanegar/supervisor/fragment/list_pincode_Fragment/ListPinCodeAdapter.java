package com.varanegar.supervisor.fragment.list_pincode_Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.ui.foldingcell.FoldingCell;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.fragment.list_notification_Fragment.ListNotificationAdapter;
import com.varanegar.supervisor.webapi.model_new.datamanager.CustomerPinModel;

import java.util.List;

public class ListPinCodeAdapter extends  RecyclerView.Adapter<ListPinCodeAdapter.ViewHolder> {

    private Context _context;
    private List<CustomerPinModel> _customerPinModel;
    public ListPinCodeAdapter(Context context, List<CustomerPinModel> customerPinModel){
        _context=context;
        _customerPinModel=customerPinModel;
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
        //header layout
        holder.txt_name_customer.setText(_customerPinModel.get(position).CustomerName);
        holder.txt_customer_code.setText(_customerPinModel.get(position).CustomerCode);
        holder.txt_date.setText(_customerPinModel.get(position).PinPDate);
        holder.txt_date.setText(_customerPinModel.get(position).PinPDate);
        holder.txt_delear_name.setText(_customerPinModel.get(position).DealerName);

        //sub layout
        holder.txt_customer_name_sub.setText(_customerPinModel.get(position).CustomerName);
        holder.txt_pincod1.setText(_customerPinModel.get(position).Pin1);
        holder.txt_pincod2.setText(_customerPinModel.get(position).Pin2);
        holder.txt_pincod3.setText(_customerPinModel.get(position).Pin3);
        holder.txt_pincod4.setText(_customerPinModel.get(position).Pin4);
        holder.content_name_view.setText(_customerPinModel.get(position).DealerName);


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
        return _customerPinModel.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private FoldingCell folding_cell;
        private TextView txt_name_customer,txt_customer_code,txt_date,txt_customer_name_sub
                ,txt_pincod1,txt_pincod2,txt_pincod3,txt_pincod4,txt_delear_name,content_name_view;
        private CardView layout_header,layout_sub;
        public ViewHolder(View itemView) {
            super(itemView);
            folding_cell= (FoldingCell) itemView.findViewById(R.id.folding_cell);
            //header layout
            txt_name_customer=itemView.findViewById(R.id.txt_name_customer_header);
            txt_customer_code=itemView.findViewById(R.id.txt_customer_code);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_delear_name=itemView.findViewById(R.id.txt_delear_name);
            layout_header=itemView.findViewById(R.id.layout_header);

            //sub layout
            txt_customer_name_sub=itemView.findViewById(R.id.txt_customer_name_sub);
            txt_pincod1=itemView.findViewById(R.id.txt_pincod1);
            txt_pincod2=itemView.findViewById(R.id.txt_pincod2);
            txt_pincod3=itemView.findViewById(R.id.txt_pincod3);
            txt_pincod4=itemView.findViewById(R.id.txt_pincod4);
            layout_sub=itemView.findViewById(R.id.layout_sub);
            content_name_view=itemView.findViewById(R.id.content_name_view);


        }
    }
}
