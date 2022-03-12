package com.varanegar.supervisor.fragment.menuTools_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.fragment.menuTools_Fragment.model.Item;
import com.varanegar.supervisor.fragment.news_fragment.News_Fragment;

import java.util.List;

public class MenuToolsAdapter  extends RecyclerView.Adapter<MenuToolsAdapter.ViewHolder> {
    private RecyclerView parentRecycler;
    private List<Item> data;
    private Context mcontext;

    private ItemClickListener mItemClickListener;

    public MenuToolsAdapter(Context context,List<Item> data) {
        this.data = data;
        this.mcontext=context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_menutools_card, parent, false);
        return new ViewHolder(v);
    }
    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(data.get(position).getItemIcon())
                .into(holder.imageView);
        holder.textView.setText(data.get(position).getNameItem());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;
        private CardView card_item;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.city_image);
            textView = itemView.findViewById(R.id.city_name);
            card_item= itemView.findViewById(R.id.card_item);
            itemView.findViewById(R.id.container).setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
           // parentRecycler.smoothScrollToPosition(getAdapterPosition());
            Log.e("TAG", "onClick:"+getAdapterPosition());
            int position=getAdapterPosition();
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }

        }
    }
    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position);
    }

}
