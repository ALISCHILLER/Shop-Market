package com.varanegar.vaslibrary.ui.dialog.choiceprize;

/**
 * Created by A.Jafarzadeh on 12/24/2017.
 */

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;

/**
 * Created by A.Jafarzadeh on 12/19/2017.
 */

public class ProductChoicePrizeAdapter extends RecyclerView.Adapter<ProductChoicePrizeAdapter.ViewHolder> {

    private AppCompatActivity activity;
    List<OrderPrizeViewModel> orderPrizeViewModels = new ArrayList<>();
    private BigDecimal totalQty = BigDecimal.ZERO;
    ArrayList<DiscountOrderPrizeViewModel> discountOrderPrizeViewModels = new ArrayList<>();

    public ProductChoicePrizeAdapter(List<OrderPrizeViewModel> orderPrizeViewModels, BigDecimal totalQty, @NonNull AppCompatActivity activity, ArrayList<DiscountOrderPrizeViewModel> discountOrderPrizeViewModels) {
        this.orderPrizeViewModels = orderPrizeViewModels;
        this.totalQty = totalQty;
        this.activity = activity;
        this.discountOrderPrizeViewModels = discountOrderPrizeViewModels;
    }

    public List<OrderPrizeViewModel> getOrderPrizeViewModel() {
        return orderPrizeViewModels;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productNameCode;
        public ImageView addButton, minesButton;
        public EditText prizeQty;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            productNameCode = (TextView) itemView.findViewById(R.id.product_name_and_code);
            addButton = (ImageView) itemView.findViewById(R.id.add_button);
            minesButton = (ImageView) itemView.findViewById(R.id.mines_button);
            prizeQty = (EditText) itemView.findViewById(R.id.prize_qty);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.chocie_prize_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void addOnetoItem(final int position) {
        orderPrizeViewModels.get(position).TotalQty = orderPrizeViewModels.get(position).TotalQty.add(BigDecimal.ONE);
        ItemChanged(position);
    }

    public void minesOneItem(int position) {
        orderPrizeViewModels.get(position).TotalQty = orderPrizeViewModels.get(position).TotalQty.subtract(BigDecimal.ONE);
        ItemChanged(position);
    }

    public boolean allFromOne(int position) {
        orderPrizeViewModels.get(position).TotalQty = totalQty;
        ItemChanged(position);
        return true;
    }

    public boolean nonFromOne(int position) {
        orderPrizeViewModels.get(position).TotalQty = BigDecimal.ZERO;
        ItemChanged(position);
        return false;
    }

    public void afterChanged(int position, final ViewHolder holder) {
        if (holder.prizeQty.getText().toString().equals(""))
            orderPrizeViewModels.get(position).TotalQty = BigDecimal.ZERO;
        else
            orderPrizeViewModels.get(position).TotalQty = new BigDecimal(holder.prizeQty.getText().toString());
        ItemChanged(position);
    }

    public synchronized void ItemChanged(final int position) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyItemChanged(position);
            }
        };
        handler.post(r);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //                return nonFromOne(position);
        //minesOneItem(position);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            final DiscountOrderPrizeViewModel[] myDiscountOrderPrizeViewModel = {null};
            for (DiscountOrderPrizeViewModel discountOrderPrizeViewModel : discountOrderPrizeViewModels) {
                if (orderPrizeViewModels.get(position).GoodsRef == discountOrderPrizeViewModel.goodsRef)
                    myDiscountOrderPrizeViewModel[0] = discountOrderPrizeViewModel;
            }
            holder.productNameCode.setText(orderPrizeViewModels.get(position).ProductName + " (" + myDiscountOrderPrizeViewModel[0].qty + ")");
            holder.prizeQty.setText(String.valueOf(discountOrderPrizeViewModels.get(position).totalQTy.setScale(0, BigDecimal.ROUND_FLOOR)));
            holder.addButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    discountOrderPrizeViewModels.get(position).totalQTy = myDiscountOrderPrizeViewModel[0].totalQTy;
                    holder.prizeQty.setText(String.valueOf(discountOrderPrizeViewModels.get(position).totalQTy.setScale(0, BigDecimal.ROUND_FLOOR)));
                    return true;
//                return allFromOne(position);
                }
            });
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (discountOrderPrizeViewModels.get(position).totalQTy.compareTo(new BigDecimal(myDiscountOrderPrizeViewModel[0].qty)) < 0) {
                        discountOrderPrizeViewModels.get(position).totalQTy = discountOrderPrizeViewModels.get(position).totalQTy.add(BigDecimal.ONE);
                        holder.prizeQty.setText(String.valueOf(discountOrderPrizeViewModels.get(position).totalQTy.setScale(0, BigDecimal.ROUND_FLOOR)));
                        //addOnetoItem(position);
                    }

                }
            });
            holder.minesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (discountOrderPrizeViewModels.get(position).totalQTy.compareTo(BigDecimal.ZERO) > 0) {
                        discountOrderPrizeViewModels.get(position).totalQTy = discountOrderPrizeViewModels.get(position).totalQTy.subtract(BigDecimal.ONE);
                        holder.prizeQty.setText(String.valueOf(discountOrderPrizeViewModels.get(position).totalQTy.setScale(0, BigDecimal.ROUND_FLOOR)));
                        //minesOneItem(position);
                    }
                }
            });
            holder.minesButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    discountOrderPrizeViewModels.get(position).totalQTy = BigDecimal.ZERO;
                    holder.prizeQty.setText(String.valueOf(discountOrderPrizeViewModels.get(position).totalQTy.setScale(0, BigDecimal.ROUND_FLOOR)));
//                return nonFromOne(position);
                    return true;
                }
            });
        } else {
            holder.productNameCode.setText(orderPrizeViewModels.get(position).ProductName);
            holder.prizeQty.setText(String.valueOf(orderPrizeViewModels.get(position).TotalQty.setScale(0, BigDecimal.ROUND_FLOOR)));
            holder.addButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    orderPrizeViewModels.get(position).TotalQty = totalQty;
                    holder.prizeQty.setText(String.valueOf(orderPrizeViewModels.get(position).TotalQty.setScale(0, BigDecimal.ROUND_FLOOR)));
                    return true;
//                return allFromOne(position);
                }
            });
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderPrizeViewModels.get(position).TotalQty.compareTo(totalQty) < 0) {
                        orderPrizeViewModels.get(position).TotalQty = orderPrizeViewModels.get(position).TotalQty.add(BigDecimal.ONE);
                        holder.prizeQty.setText(String.valueOf(orderPrizeViewModels.get(position).TotalQty.setScale(0, BigDecimal.ROUND_FLOOR)));
                        //addOnetoItem(position);
                    }

                }
            });
            holder.minesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderPrizeViewModels.get(position).TotalQty.compareTo(BigDecimal.ZERO) > 0) {
                        orderPrizeViewModels.get(position).TotalQty = orderPrizeViewModels.get(position).TotalQty.subtract(BigDecimal.ONE);
                        holder.prizeQty.setText(String.valueOf(orderPrizeViewModels.get(position).TotalQty.setScale(0, BigDecimal.ROUND_FLOOR)));
                        //minesOneItem(position);
                    }
                }
            });
            holder.minesButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    orderPrizeViewModels.get(position).TotalQty = BigDecimal.ZERO;
                    holder.prizeQty.setText(String.valueOf(orderPrizeViewModels.get(position).TotalQty.setScale(0, BigDecimal.ROUND_FLOOR)));
//                return nonFromOne(position);
                    return true;
                }
            });
        }
        holder.prizeQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (holder.prizeQty.getText().toString().equals(""))
                    orderPrizeViewModels.get(position).TotalQty = BigDecimal.ZERO;
                else
                    orderPrizeViewModels.get(position).TotalQty = new BigDecimal(holder.prizeQty.getText().toString());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return orderPrizeViewModels.size();
    }
}