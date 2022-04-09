package com.varanegar.supervisor.fragment.list_notification_Fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import java.util.List;

public class ListNotificationAdapter extends
        RecyclerView.Adapter<ListNotificationAdapter.ViewHolder> {
     private Context _context;
     private List<PinRequest_Model> _pinRequestModels;

    public  ListNotificationAdapter(Context context, List<PinRequest_Model> pinRequestModels){
        _pinRequestModels=pinRequestModels;
        _context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_listnotification_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text_customerId.setText(_pinRequestModels.get(position).customerId.toString());
        holder.text_customerName.setText(_pinRequestModels.get(position).customerName);
        holder.text_dealerName.setText(_pinRequestModels.get(position).dealerName);
        holder.text_pinName.setText(_pinRequestModels.get(position).pinName);
        holder.text_date.setText(_pinRequestModels.get(position).date.toString());
    }

    @Override
    public int getItemCount() {
        return _pinRequestModels.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text_customerId,text_customerName,text_dealerName,text_pinName,text_date;
        private Button btn_true_request,btn_false_request;
        public ViewHolder(View itemView) {
            super(itemView);
            text_customerId=itemView.findViewById(R.id.text_customerId);
            text_customerName=itemView.findViewById(R.id.text_customerName);
            text_dealerName=itemView.findViewById(R.id.text_dealerName);
            text_pinName=itemView.findViewById(R.id.text_pinName);
            text_date=itemView.findViewById(R.id.text_date);
        }
    }
}
