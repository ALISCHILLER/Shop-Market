package com.varanegar.supervisor.fragment.list_notification_Fragment;

import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.ui.gradientlayout.GradientRelativeLayout;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import com.varanegar.supervisor.fragment.menuTools_Fragment.MenuToolsAdapter;
import com.varanegar.vaslibrary.webapi.freereason.IFreeReasonApi;

import java.util.List;

public class ListNotificationAdapter extends
        RecyclerView.Adapter<ListNotificationAdapter.ViewHolder> {
     private Context _context;
     private List<PinRequest_Model> _pinRequestModels;
    private ItemClickListener mItemClickListener;
    public  ListNotificationAdapter(Context context, List<PinRequest_Model> pinRequestModels){
        _pinRequestModels=pinRequestModels;
        _context=context;
    }
    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_listnotification_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView")
            int position) {

        holder.text_customerId.setText(_pinRequestModels.get(position).customerId.toString());
        holder.text_customerName.setText(_pinRequestModels.get(position).customerName);
        holder.text_dealerName.setText(_pinRequestModels.get(position).dealerName);
        holder.text_pinName.setText(_pinRequestModels.get(position).pinName);
        holder.text_date.setText(_pinRequestModels.get(position).date.toString());

        holder.btn_true_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick("Approve",_pinRequestModels.get(position));
                }
            }
        });
        holder.btn_false_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick("Reject",_pinRequestModels.get(position));
                    setviewonclick(holder);
                }
            }
        });
        if (_pinRequestModels.get(position).Status.equals("Visible")){
            setviewonclick(holder);
        }else {
            int start_color = ContextCompat.getColor(_context, R.color.gradientLightGreen);
            int end_color = ContextCompat.getColor(_context, R.color.gradientLightBlue);
            holder.gradient_top.setStartColor(start_color);
            holder.gradient_top.setOrientation(LEFT_RIGHT);
            holder.gradient_top.setEndColor(end_color);
        }
    }

    @Override
    public int getItemCount() {
        return _pinRequestModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text_customerId,text_customerName,text_dealerName,text_pinName,text_date;
        private Button btn_true_request,btn_false_request;
        private GradientRelativeLayout gradient_top;
        public ViewHolder(View itemView) {
            super(itemView);
            text_customerId=itemView.findViewById(R.id.text_customerId);
            text_customerName=itemView.findViewById(R.id.text_customerName);
            text_dealerName=itemView.findViewById(R.id.text_dealerName);
            text_pinName=itemView.findViewById(R.id.text_pinName);
            text_date=itemView.findViewById(R.id.text_date);
            btn_true_request=itemView.findViewById(R.id.btn_true_request);
            btn_false_request=itemView.findViewById(R.id.btn_false_request);
            gradient_top=itemView.findViewById(R.id.gradient_top);
        }
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(String Status,PinRequest_Model pinRequest_model);
    }
    public void setviewonclick(@NonNull ViewHolder holder){
        holder.btn_true_request.setVisibility(View.GONE);
        holder.btn_false_request.setVisibility(View.GONE);
        int end_color = ContextCompat.getColor(_context, R.color.cardview_dark_background);
        holder.gradient_top.setStartColor(end_color);
        holder.gradient_top.setOrientation(LEFT_RIGHT);
        holder.gradient_top.setEndColor(end_color);
    }
}
