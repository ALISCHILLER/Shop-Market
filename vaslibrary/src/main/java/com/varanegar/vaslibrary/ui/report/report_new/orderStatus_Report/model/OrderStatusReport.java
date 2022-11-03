package com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderStatusReport implements Parcelable {

    @SerializedName("date")
    private String date;
    @SerializedName("orderWeight")
    private Double orderWeight;
    @SerializedName("pendingOrderWeight")
    private Double pendingOrderWeight;
    @SerializedName("inProgressOrderWeight")
    private Double inProgressOrderWeight;
    @SerializedName("undeliverdOrderWeight")
    private Double undeliverdOrderWeight;
    @SerializedName("finalWeight")
    private Double finalWeight;
    private List<DealersItem> dealersItems;

    protected OrderStatusReport(Parcel in) {
        date = in.readString();
        if (in.readByte() == 0) {
            orderWeight = null;
        } else {
            orderWeight = in.readDouble();
        }
        if (in.readByte() == 0) {
            pendingOrderWeight = null;
        } else {
            pendingOrderWeight = in.readDouble();
        }
        if (in.readByte() == 0) {
            inProgressOrderWeight = null;
        } else {
            inProgressOrderWeight = in.readDouble();
        }
        if (in.readByte() == 0) {
            undeliverdOrderWeight = null;
        } else {
            undeliverdOrderWeight = in.readDouble();
        }
        if (in.readByte() == 0) {
            finalWeight = null;
        } else {
            finalWeight = in.readDouble();
        }
        isExpand = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        if (orderWeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(orderWeight);
        }
        if (pendingOrderWeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pendingOrderWeight);
        }
        if (inProgressOrderWeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(inProgressOrderWeight);
        }
        if (undeliverdOrderWeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(undeliverdOrderWeight);
        }
        if (finalWeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(finalWeight);
        }
        dest.writeByte((byte) (isExpand ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderStatusReport> CREATOR = new Creator<OrderStatusReport>() {
        @Override
        public OrderStatusReport createFromParcel(Parcel in) {
            return new OrderStatusReport(in);
        }

        @Override
        public OrderStatusReport[] newArray(int size) {
            return new OrderStatusReport[size];
        }
    };

    public List<DealersItem> getDealersItems() {
        return dealersItems;
    }

    public void setDealersItems(List<DealersItem> dealersItems) {
        this.dealersItems = dealersItems;
    }

    private boolean isExpand;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(Double orderWeight) {
        this.orderWeight = orderWeight;
    }

    public Double getPendingOrderWeight() {
        return pendingOrderWeight;
    }

    public void setPendingOrderWeight(Double pendingOrderWeight) {
        this.pendingOrderWeight = pendingOrderWeight;
    }

    public Double getInProgressOrderWeight() {
        return inProgressOrderWeight;
    }

    public void setInProgressOrderWeight(Double inProgressOrderWeight) {
        this.inProgressOrderWeight = inProgressOrderWeight;
    }

    public Double getUndeliverdOrderWeight() {
        return undeliverdOrderWeight;
    }

    public void setUndeliverdOrderWeight(Double undeliverdOrderWeight) {
        this.undeliverdOrderWeight = undeliverdOrderWeight;
    }

    public Double getFinalWeight() {
        return finalWeight;
    }

    public void setFinalWeight(Double finalWeight) {
        this.finalWeight = finalWeight;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
